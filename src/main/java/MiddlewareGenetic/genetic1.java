/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
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
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;


/**
 *
 * @author FD_gi
 */
public class genetic1 {
    
    private static final int VM_PES = 1;
    
    private static final int  HOSTS = 10;
    private static final int  HOST_PES = 10;
    private static final int  HOST_MIPS = 10000; // Milion Instructions per Second (MIPS)
    private static final int  HOST_RAM = 5000; //in Megabytes
    private static final long HOST_BW = 10_000; //in Megabits/s
    private static final long HOST_STORAGE = 1_000_000; //in Megabytes

    private static final int CLOUDLETS = 100;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 10_000; // Milion Instructions (MI)
    
    private final CloudSimPlus simulation;
    private final DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    
    public static void main(String[] args) {
        new genetic1();
    }
    public genetic1() {
        simulation = new CloudSimPlus();
        datacenter0 = createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);
                
        int num_cloudlet = 10;
        int num_vm = 10;
        
        vmList = createVms(num_vm); // creating 20 vms
        
        for (int i=0;i<vmList.size();i++){
            System.out.println(vmList.get(i).getTotalMipsCapacity());
        }
            
        
        cloudletList = createCloudlets(num_cloudlet); // creating 40 cloudlets
     
        
        //Poblacion inicial
        //Se crea un array con Poblaciones. Habran n=num_cloudlets Poblaciones. 
        //Con n=10 habran 10 poblaciones. Cada indice de initialPopulation tiene un array de Individuos
        ArrayList<Poblacion> initialPopulation = new ArrayList<Poblacion>();
        for(int j=0;j<num_cloudlet;j++)
        {
                ArrayList<Individuo> firstChromosome = new ArrayList<Individuo>();

                for(int i=0;i<num_cloudlet;i++)
                {
                        int k=(i+j)%num_vm;
                        k=(k+num_cloudlet)%num_cloudlet;
                        Individuo geneObj = new Individuo(cloudletList.get(i),vmList.get(k));
                        firstChromosome.add(geneObj);
                }
                Poblacion chromosome = new Poblacion(firstChromosome);
                initialPopulation.add(chromosome);
        }
        
        for (int i=0; i<initialPopulation.size();i++){
            //System.out.println("Poulation: "+initialPopulation.get(i).getIndividuosList().get(i).getCloudletFromIndividuo().getLength());
            System.out.println("Poulation: "+initialPopulation.get(i).getIndividuosList().size());
        }


        int generation=20;
        

        
        int populationSize=initialPopulation.size();
        Random random = new Random();
        // Running the algorithm for 20 generations
        for(int count=0;count<=generation;count++){
                        
                        //se toman dos poblaciones de manera random
			int index1,index2;
			index1=random.nextInt(populationSize) % populationSize;
			index2=random.nextInt(populationSize) % populationSize;
			ArrayList<Individuo> l1= new ArrayList<Individuo>();
                        System.out.println("Index1: "+index1);
                        System.out.println("Index2: "+index2);
			l1=initialPopulation.get(index1).getIndividuosList();
			Poblacion cromosoma1 = new Poblacion(l1);
			ArrayList<Individuo> l2= new ArrayList<Individuo>();
			l2=initialPopulation.get(index2).getIndividuosList();
			Poblacion cromosoma2 = new Poblacion(l2);
			double rangeMin = 0.0f;

                        double rangeMax = 1.0f;
                        Random r = new Random();
                        double crossProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                        //en caso de que la probabilidad calculada de manera random sea menor a 0.5 se realiza crossover de invdividuos random
                        if(crossProb<0.5)
                        {
                                int ind=0 + (int)(Math.random() * ((num_cloudlet-1 - 0) + 1));
                                
                                //el crossover se realiza intercambiando las Vms de un index random
                                
                                Vm vm1 = l1.get(ind).getVmFromaIndividuo();
                                Vm vm2 = l2.get(ind).getVmFromaIndividuo();
                                cromosoma1.updateIndividuo(ind, vm2);
                                cromosoma2.updateIndividuo(ind, vm1);
                                initialPopulation.set(index1, cromosoma1);
                                initialPopulation.set(index2, cromosoma2);

                                

                        }

            
        }
        
        int fittestIndex=0;
        double time=1000000;
        
        //se itera sobre las poblaciones buscando la poblacion con mejor fitteest, que se evalua sumando las divisiones entre cloudlet.length y vm.Mips
        for(int i=0;i<initialPopulation.size();i++)
        {
                ArrayList<Individuo> l= new ArrayList<Individuo>();
                l=initialPopulation.get(i).getIndividuosList();
                double sum=0;
                for(int j=0;j<num_cloudlet;j++)
                {
                        Individuo g = l.get(j);
                        Cloudlet c = g.getCloudletFromIndividuo();
                        Vm v = g.getVmFromaIndividuo();
                        double temp = c.getLength()/v.getMips();
                        sum+=temp;
                }
                if(sum<time)
                {
                        time=sum;
                        fittestIndex=i;
                }
        }
        System.out.println("Fittest: "+fittestIndex);
        
        
        ArrayList<Individuo> result = new ArrayList<Individuo>();
        result = initialPopulation.get(fittestIndex).getIndividuosList();
        for (int i=0;i<initialPopulation.size();i++){
            for (int j=0;j<initialPopulation.get(i).getIndividuosList().size();j++){
                System.out.println("Initial: "+initialPopulation.get(i).getIndividuosList().get(j).getCloudletFromIndividuo().getLength());
            }
            System.out.println(".............");
            
        }
        System.out.println("Size de result: "+result.size());
        System.out.println(initialPopulation);
        System.out.println(result);

        List<Cloudlet> finalcloudletList = new ArrayList<Cloudlet>();
        List<Vm> finalvmlist = new ArrayList<Vm>();



        //se toman las cloudlet y vms. Se asigna cada Cloudlet a su respectiva Vm
        for(int i=0;i<result.size();i++)
        {
            
                Vm vm=result.get(i).getVmFromaIndividuo();
                finalvmlist.add(vm);
                
                Cloudlet cloudlet=result.get(i).getCloudletFromIndividuo();
                cloudlet.setVm(vm);
                finalcloudletList.add(cloudlet);
                

        }

        for (int i=0;i<finalvmlist.size();i++){
            System.out.println(finalvmlist.get(i).getTotalMipsCapacity());
        }
        //se asignan las vm y cloudlet al broker
        
        broker0.submitVmList(finalvmlist);
        broker0.submitCloudletList(finalcloudletList);

        // Starts the simulation
        simulation.start();

        // Print results when simulation is over


        final var cloudletFinishedList = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudletFinishedList).build();


 
       
    }
    
    
    
    
    
    
    
    
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
        final var vm = new VmSimple(1000, VM_PES);
        vm.setRam(512).setBw(1000).setSize(1000);
        return vm;
    }
    private Vm createVmMedium(){
        final var vm = new VmSimple(1500, VM_PES);
        vm.setRam(1024).setBw(1500).setSize(2000);
        return vm;
    }    
    private Vm createVmAdvanced(){
        final var vm = new VmSimple(3000, VM_PES);
        vm.setRam(2048).setBw(2000).setSize(3000);
        return vm;
    }    
    

    
    private List<Vm> createVms(int num_vm) {
        final var vmList = new ArrayList<Vm>(num_vm);
        for (int i = 0; i < num_vm; i++) {
            Random rand = new Random();
            int x = rand.nextInt(3);
            switch (x) {
                case 0:
                    vmList.add(createVmBasic());
                    break;
                case 1:
                    vmList.add(createVmMedium());
                    break;
                case 2:
                    vmList.add(createVmAdvanced());
                    break;
                default:
                    break;
            }
            
        }

        return vmList;
    }
    private List<Cloudlet> createCloudlets(int num_cloudlet) {
        final var cloudletList = new ArrayList<Cloudlet>(num_cloudlet);
        long length = 1000;
        int pesNumber = 1;
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final var utilizationModel =  new UtilizationModelFull();

        for (int i = 0; i < num_cloudlet; i++) {
            int x = (int) (Math.random() * ((2000 - 1) + 1)) + 1;
            final var cloudlet = new CloudletSimple(length+x, pesNumber, utilizationModel);
            cloudlet.setSizes(1024);
            cloudletList.add(cloudlet);
        }

        return cloudletList;
    }
}
