/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.CsvTable;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

/**
 *
 * @author Fede
 */
public class Secuencial {
    private static final int  HOSTS = 100;
    private static final int  HOST_PES = 32;
    private static final int  HOST_MIPS = 10000; // Milion Instructions per Second (MIPS)
    private static final int  HOST_RAM = 65_536; //in Megabytes
    private static final long HOST_BW = 10_000; //in Megabits/s
    private static final long HOST_STORAGE = 1_000_000; //in Megabytes

    private static final int VMS = 1;
    private static final int VM_PES = 4;

    private static final int BASIC_VM_MIPS=1000;
    private static final int BASIC_VM_RAM=512;
    private static final int BASIC_VM_BW=1000;
    private static final int BASIC_VM_SIZE=100;
    
    private static final int MEDIUM_VM_MIPS=5000;
    private static final int MEDIUM_VM_RAM=1024;
    private static final int MEDIUM_VM_BW=1500;
    private static final int MEDIUM_VM_SIZE=2000;
    
    
    private static final int ADVANCED_VM_MIPS=10_000;
    private static final int ADVANCED_VM_RAM=2048;
    private static final int ADVANCED_VM_BW=2000;
    private static final int ADVANCED_VM_SIZE=3000;
    
    //0 for BASIC, 1 FOR MEDIUM, 2 FOR ADVANCED
    private static final int TYPE_OF_VM_USED=0;
    
    
    
    private static final int CLOUDLETS = 300;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 400_000; // Milion Instructions (MI)
    private static final int CLOUDLET_SIZE=1024;            
    private static final int CLOUDLET_FileSize=1024;         
    private static final int CLOUDLET_OutputSize=1024;            

    private final CloudSimPlus simulation;
    private final DatacenterBrokerSimple broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    
    
    public static void main(String[] args) {
        new Secuencial();
    }
    private Secuencial() {
        simulation = new CloudSimPlus();
        datacenter0 = createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);
                
        
        vmList = createVms(); // creating n vms
        System.out.println("CANTIDAD DE VMS: "+vmList.size());
        
        
        cloudletList = createCloudlets(CLOUDLETS); // creating 40 cloudlets
        
        //asignacion de cloudlets en Vms de manera random
        for (int k=0; k<CLOUDLETS; k++){
              int rndIndex = (int)(Math.random() * (vmList.size()-1));
              cloudletList.get(k).setVm(vmList.get(rndIndex));
              System.out.println("Cloudlet: "+k);
              System.out.println("Vm: "+rndIndex);

        }
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);
        
        simulation.start();
        System.out.println(        datacenter0.getHostList());


        final var cloudletFinishedList = broker0.getCloudletFinishedList();
        //new CloudletsTableBuilder(cloudletFinishedList).build();
        final var table = new CloudletsTableBuilder(cloudletFinishedList);
        table.build();
        table.getTable();
        try {
            CsvTable csv = new CsvTable();
            LocalTime date = LocalTime.now();
            
            csv.setPrintStream(new PrintStream(new java.io.File("results"+date.getHour()+date.getMinute()+date.getSecond() + ".csv")));
            new CloudletsTableBuilder(broker0.getCloudletFinishedList(), csv).build();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("CANTIDAD DE CLOUDLET EJECUTADAS: "+cloudletFinishedList.size());
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final var hostList = new ArrayList<Host>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            final var host = createHost();
            hostList.add(host);
        }

        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final var peList = new ArrayList<Pe>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(HOST_MIPS));
        }

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
    }

    private Vm createVmBasic(){
        final var vm = new VmSimple(BASIC_VM_MIPS, VM_PES);
        vm.setRam(BASIC_VM_RAM).setBw(BASIC_VM_BW).setSize(BASIC_VM_SIZE);
        return vm;
    }
    private Vm createVmMedium(){
        final var vm = new VmSimple(MEDIUM_VM_MIPS, VM_PES);
        vm.setRam(MEDIUM_VM_RAM).setBw(MEDIUM_VM_BW).setSize(MEDIUM_VM_SIZE);
        return vm;
    }    
    private Vm createVmAdvanced(){
        final var vm = new VmSimple(ADVANCED_VM_MIPS, VM_PES);
        vm.setRam(ADVANCED_VM_RAM).setBw(ADVANCED_VM_BW).setSize(ADVANCED_VM_SIZE);
        return vm;
    }    
    

    //devuelve una lista con Vms de diferentes categorias
    //20% de numVm son basicas, 40% medias y 40% avanzadas
    private List<Vm> createVms() {
        final var vmList = new ArrayList<Vm>(VMS);

        switch(TYPE_OF_VM_USED){
            case 0:
                vmList.add(createVmBasic());
                break;
            case 1:
                vmList.add(createVmMedium());
                break;
            case 2:
                vmList.add(createVmAdvanced());
                break;
                
        }


        return vmList;
    }

    /**
     * Creates a list of Cloudlets.
     */

    
    private List<Cloudlet> createCloudlets(int num_cloudlet) {
        final var cloudletList = new ArrayList<Cloudlet>(num_cloudlet);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        //final var utilizationModel =  new UtilizationModelFull();
        //final var utilizationModel =  new UtilizationModelDynamic(0.1);

        for (int i = 0; i < num_cloudlet; i++) {
            //int x = (int) (Math.random() * ((2000 - 1) + 1)) + 1;
            //final var cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            final var cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES);
            cloudlet.setSizes(CLOUDLET_SIZE);
            cloudlet.setFileSize(CLOUDLET_FileSize);
            cloudlet.setOutputSize(CLOUDLET_OutputSize);
            cloudletList.add(cloudlet);
        }

        return cloudletList;
    }
}
