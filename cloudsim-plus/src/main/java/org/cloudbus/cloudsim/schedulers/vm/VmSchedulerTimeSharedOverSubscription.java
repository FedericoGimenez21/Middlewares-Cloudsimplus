/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * This is a Time-Shared VM Scheduler, which allows over-subscription. In other
 * words, the scheduler still allows the allocation of VMs that require more CPU
 * capacity than is available. Oversubscription results in performance
 * degradation.
 *
 * @author Anton Beloglazov
 * @author Rodrigo N. Calheiros
 * @since CloudSim Toolkit 3.0
 */
public class VmSchedulerTimeSharedOverSubscription extends VmSchedulerTimeShared {

    /**
     * Instantiates a new vm scheduler time shared over subscription.
     *
     */
    public VmSchedulerTimeSharedOverSubscription() {
        super();
    }

    /**
     * Allocates PEs for vm. The policy allows over-subscription. In other
     * words, the policy still allows the allocation of VMs that require more
     * CPU capacity than is available. Oversubscription results in performance
     * degradation. It cannot be allocated more CPU capacity for each virtual PE
     * than the MIPS capacity of a single physical PE.
     *
     * @param vm the vm
     * @param mipsShareRequested the list of mips share requested
     * @return true, if successful
     */
    @Override
    protected boolean updateMapOfRequestedMipsForVm(Vm vm, List<Double> mipsShareRequested) {
        double totalRequestedMips = 0;

        // if the requested mips is bigger than the capacity of a single PE, we cap
        // the request to the PE's capacity
        List<Double> mipsShareRequestedCapped = new ArrayList<>();
        double peMips = getPeCapacity();
        for (double mips : mipsShareRequested) {
            if (mips > peMips) {
                mipsShareRequestedCapped.add(peMips);
                totalRequestedMips += peMips;
            } else {
                mipsShareRequestedCapped.add(mips);
                totalRequestedMips += mips;
            }
        }

        getMipsMapRequested().put(vm, mipsShareRequested);
        setPesInUse(getPesInUse() + mipsShareRequested.size());

        if (getVmsMigratingIn().contains(vm)) {
            // the destination host only experience 10% of the migrating VM's MIPS
            totalRequestedMips *= 0.1;
        }

        if (getAvailableMips() >= totalRequestedMips) {
            List<Double> mipsShareAllocated = new ArrayList<>();
            for (double mipsRequested : mipsShareRequestedCapped) {
                if (getVmsMigratingOut().contains(vm)) {
                    // performance degradation due to migration = 10% MIPS
                    mipsRequested *= 0.9;
                } else if (getVmsMigratingIn().contains(vm)) {
                    // the destination host only experience 10% of the migrating VM's MIPS
                    mipsRequested *= 0.1;
                }
                mipsShareAllocated.add(mipsRequested);
            }

            getMipsMapAllocated().put(vm, mipsShareAllocated);
            setAvailableMips(getAvailableMips() - totalRequestedMips);
        } else {
            redistributeMipsDueToOverSubscription();
        }

        return true;
    }

    /**
     * Recalculates distribution of MIPs among VMs, considering eventual
     * shortage of MIPS compared to the amount requested by VMs.
     */
    protected void redistributeMipsDueToOverSubscription() {
        // First, we calculate the scaling factor - the MIPS allocation for all VMs will be scaled proportionally

        Map<Vm, List<Double>> mipsMapCap = new HashMap<>();
        double totalRequiredMipsByAllVms = getTotalRequiredMipsByAllVms(mipsMapCap);

        double totalAvailableMips = PeList.getTotalMips(getPeList());
        double scalingFactor = totalAvailableMips / totalRequiredMipsByAllVms;

        updateActualMipsAllocatedToVms(mipsMapCap, scalingFactor);

        // As the host is oversubscribed, there is no more available MIPS
        setAvailableMips(0);
    }

    private void updateActualMipsAllocatedToVms(Map<Vm, List<Double>> mipsMapCap, double scalingFactor) {
        // Clear the old MIPS allocation
        getMipsMapAllocated().clear();
        for (Entry<Vm, List<Double>> entry : mipsMapCap.entrySet()) {
            Vm vm = entry.getKey();
            List<Double> requestedMips = entry.getValue();

            List<Double> updatedMipsAllocation = new ArrayList<>();
            for (double mips : requestedMips) {
                if (getVmsMigratingOut().contains(vm)) {
                    // the original amount is scaled
                    mips *= scalingFactor;
                    // performance degradation due to migration = 10% MIPS
                    mips *= 0.9;
                } else if (getVmsMigratingIn().contains(vm)) {
                    // the destination host only experiences 10% of the migrating VM's MIPS
                    mips *= 0.1;
                    // the final 10% of the requested MIPS are scaled
                    mips *= scalingFactor;
                } else {
                    mips *= scalingFactor;
                }

                updatedMipsAllocation.add(mips);
            }

            // add in the new map
            getMipsMapAllocated().put(vm, updatedMipsAllocation);
        }
    }

    /**
     * Gets the total of MIPS required by all VMs and fills a map of MIPS cap
     * for each VM, that is, the physical MIPS capacity that each VM can use
     * for each virtual PE.
     *
     * @param mipsMapCap the map of MIPS cap to be filled
     * @return the total of MIPS required by all VMs
     */
    private double getTotalRequiredMipsByAllVms(Map<Vm, List<Double>> mipsMapCap) {
        double totalRequiredMipsByAllVms = 0;
        for (Entry<Vm, List<Double>> entry : getMipsMapRequested().entrySet()) {
            double requiredMipsByThisVm = 0.0;
            Vm vm = entry.getKey();
            List<Double> mipsShareRequested = entry.getValue();
            List<Double> mipsShareRequestedCapped = new ArrayList<>();
            double peMips = getPeCapacity();
            for (double mips : mipsShareRequested) {
                if (mips > peMips) {
                    mipsShareRequestedCapped.add(peMips);
                    requiredMipsByThisVm += peMips;
                } else {
                    mipsShareRequestedCapped.add(mips);
                    requiredMipsByThisVm += mips;
                }
            }

            mipsMapCap.put(vm, mipsShareRequestedCapped);

            if (getVmsMigratingIn().contains(entry.getKey())) {
                // the destination host only experience 10% of the migrating VM's MIPS
                requiredMipsByThisVm *= 0.1;
            }
            totalRequiredMipsByAllVms += requiredMipsByThisVm;
        }

        return totalRequiredMipsByAllVms;
    }
}
