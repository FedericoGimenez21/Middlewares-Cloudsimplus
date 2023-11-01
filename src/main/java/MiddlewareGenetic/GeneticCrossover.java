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
import java.util.Random;
import java.util.Comparator;
import org.cloudsimplus.brokers.DatacenterBroker;
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
 * @author FD_gi
 */
public class GeneticCrossover {
    private static final int  HOSTS = 100;
    private static final int  HOST_PES = 32;
    private static final int  HOST_MIPS = 10000; // Milion Instructions per Second (MIPS)
    private static final int  HOST_RAM = 65_536; //in Megabytes
    private static final long HOST_BW = 10_000; //in Megabits/s
    private static final long HOST_STORAGE = 1_000_000; //in Megabytes

    private static final int VMS = 30;
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
    
    
    
    private static final int CLOUDLETS = 50;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 100_000; // Milion Instructions (MI)
    private static final int CLOUDLET_SIZE=1024;            
    private static final int CLOUDLET_FileSize=1024;         
    private static final int CLOUDLET_OutputSize=1024;                  
    
    private final CloudSimPlus simulation;
    private final DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    
    public static void main(String[] args) {
        new GeneticCrossover();
    }
    
    public GeneticCrossover() {
        simulation = new CloudSimPlus();
        datacenter0 = createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);
                

        int initialPopulationSize = 20;
        
        vmList = createVms(VMS); // creating n vms
        System.out.println("CANTIDAD DE VMS: "+vmList.size());          
        
        cloudletList = createCloudlets(CLOUDLETS); // creating 40 cloudlets
     
        
        //Poblacion inicial
        //Se crea un array con Poblaciones. Habran n=num_vm Poblaciones. 
        //Con n=10 habran 10 poblaciones. Cada indice de initialPopulation tiene un array de Individuos
        ArrayList<Individuo> population = new ArrayList<Individuo>();
        for (int i=0; i<initialPopulationSize; i++){
            ArrayList<Cromosoma> firstIndividuo = new ArrayList<Cromosoma>();
            
            for (int j=0; j<VMS;j++){
                Cromosoma geneObj = new Cromosoma(vmList.get(j));
                firstIndividuo.add(geneObj);
            }
            
            for (int k=0; k<CLOUDLETS; k++){
                int rndIndex = (int)Math.floor(Math.random() * (vmList.size()-1));
                firstIndividuo.get(rndIndex).addCloudletForCromosoma(cloudletList.get(k));
            }
            
            Individuo individuo = new Individuo(firstIndividuo);
            population.add(individuo);
        }

        this.setFitness(population, VMS);
        
        int generation=40;
        int populationSize=population.size();
        Random random = new Random();
        // Running the algorithm for 20 generations
        for(int count=0;count<=generation;count++){
                        
            //se toman dos individuos de manera random
            int index1,index2;
            index1=0;
            index2=1;
            ArrayList<Cromosoma> l1= new ArrayList<Cromosoma>();
            l1=population.get(index1).getCromosomasList();
            Individuo hijo1 = new Individuo(l1);
            ArrayList<Cromosoma> l2= new ArrayList<Cromosoma>();
            l2=population.get(index2).getCromosomasList();
            Individuo hijo2 = new Individuo(l2);
            double rangeMin = 0.0f;

            double rangeMax = 1.0f;
            Random r = new Random();
            double crossProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            //en caso de que la probabilidad calculada de manera random sea menor a 0.5 se realiza crossover de invdividuos random
            if(crossProb<0.7)
            {   
                int separationIndex = (int) Math.floor(VMS/2);
                
                ArrayList<Cromosoma> subList1 = new ArrayList<Cromosoma>(l1.subList(0, separationIndex));
                ArrayList<Cromosoma> subList2 = new ArrayList<Cromosoma>(l2.subList(separationIndex, VMS));
                ArrayList<Cromosoma> subList3 = new ArrayList<Cromosoma>(l2.subList(0, separationIndex));
                ArrayList<Cromosoma> subList4 = new ArrayList<Cromosoma>(l1.subList(separationIndex, VMS));
                
                subList1.addAll(subList2);
                subList3.addAll(subList4);
                hijo1.setCromosomas(subList1);
                hijo2.setCromosomas(subList3);
                population.add(hijo1);
                population.add(hijo2);
                                
            }
            this.setFitness(population, VMS);
            population = new ArrayList<Individuo>(population.subList(0, 20));
        }
        
        //se itera sobre las poblaciones buscando la poblacion con mejor fitteest, que se evalua sumando las divisiones entre cloudlet.length y vm.Mips    
        
        ArrayList<Cromosoma> result = new ArrayList<Cromosoma>();
        result = population.get(0).getCromosomasList();

        List<Cloudlet> finalcloudletList = new ArrayList<Cloudlet>();
        List<Vm> finalvmlist = new ArrayList<Vm>();



        //se toman las cloudlet y vms. Se asigna cada Cloudlet a su respectiva Vm
        for(int i=0;i<result.size();i++)
        {            
            Vm vm=result.get(i).getVmFromCromosoma();
            finalvmlist.add(vm);
                
            for (int x=0; x<result.get(i).getCloudletListFromCromosoma().size();x++){
                Cloudlet cloudlet=result.get(i).getCloudletListFromCromosoma().get(x);
                cloudlet.setVm(vm);
                finalcloudletList.add(cloudlet); 
            }
        }
        
        //se asignan las vm y cloudlet al broker
        broker0.submitVmList(finalvmlist);
        broker0.submitCloudletList(finalcloudletList);

        // Starts the simulation
        simulation.start();

        // Print results when simulation is over


        final var cloudletFinishedList = broker0.getCloudletFinishedList();

        
        final var table = new CloudletsTableBuilder(cloudletFinishedList);
        table.build();
        
        System.out.println("SIZE: "+cloudletFinishedList.size());
        System.out.println("cloudlet: "+broker0.getCloudletFinishedList().get(cloudletFinishedList.size()-1).getFinishTime());
        table.getTable();
        try {
            CsvTable csv = new CsvTable();
            LocalTime date = LocalTime.now();
            
            csv.setPrintStream(new PrintStream(new java.io.File("results"+date.getHour()+date.getMinute()+date.getSecond() + ".csv")));
            new CloudletsTableBuilder(broker0.getCloudletFinishedList(), csv).build();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void setFitness(ArrayList<Individuo> population, int num_vm){
        
        int fittestIndex=0;
        
        for(int i=0;i<population.size();i++)
        {
                ArrayList<Cromosoma> l= new ArrayList<Cromosoma>();
                l=population.get(i).getCromosomasList();
                double sum=0;
                for(int j=0;j<num_vm;j++)
                {
                        Cromosoma g = l.get(j);
                        ArrayList<Cloudlet> cloudletsVm = g.getCloudletListFromCromosoma();
                        long sumLengthCloudlet=0;
                        for (int x=0;x<cloudletsVm.size();x++){
                            sumLengthCloudlet=sumLengthCloudlet+cloudletsVm.get(x).getLength();
                        }
                       
                        Vm v = g.getVmFromCromosoma();
                        double temp = sumLengthCloudlet/v.getMips();
                        sum+=temp;
                        
                }
                
                Individuo ind = population.get(i);
                ind.setFitness(sum);
        }
        
        population.sort(Comparator.comparing(Individuo::getFitness));
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
    private List<Vm> createVms(int num_vm) {
        final var vmList = new ArrayList<Vm>(num_vm);
        double porcentajeBasic=num_vm*0.20;
        int numBasic= (int) porcentajeBasic;
        double porcentajeMedium=num_vm*0.40;
        int numMedium= (int) porcentajeMedium;
        
        double porcentajeAdvanced=num_vm*0.40;
        int numAdvanced= (int) porcentajeAdvanced;
        
        for (int i=0;i<numBasic;i++){
            vmList.add(createVmBasic());
        }    
        for (int i=0; i<numMedium;i++){
            vmList.add(createVmMedium());
        }
        for (int i=0; i<numAdvanced;i++){
            
            vmList.add(createVmAdvanced());
        }

        return vmList;
    }
    
    private List<Cloudlet> createCloudlets(int num_cloudlet) {
        final var cloudletList = new ArrayList<Cloudlet>(num_cloudlet);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        //final var utilizationModel =  new UtilizationModelFull();
        final var utilizationModel =  new UtilizationModelDynamic(0.1);

        for (int i = 0; i < num_cloudlet; i++) {
            //int x = (int) (Math.random() * ((2000 - 1) + 1)) + 1;
            final var cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(CLOUDLET_SIZE);
            cloudlet.setFileSize(CLOUDLET_FileSize);
            cloudlet.setOutputSize(CLOUDLET_OutputSize);
            cloudletList.add(cloudlet);
        }

        return cloudletList;
    }
}
