/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

import java.util.ArrayList;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;


/**
 *
 * @author FD_gi
 */
public class Individuo{
	
	protected ArrayList<Cromosoma> cromosomaList;
        private double fitness;
        
        public void setCromosomas(ArrayList<Cromosoma> newList){
            this.cromosomaList = newList;
        }
        
        public void setFitness(double newFitness){
            this.fitness = newFitness;
        }
        
        public double getFitness(){
            return this.fitness;
        }
	
	public Individuo(ArrayList<Cromosoma> individuosList){
		this.cromosomaList=individuosList;		
	}
	
	public ArrayList<Cromosoma> getCromosomasList(){
		return this.cromosomaList;
	}
	
	public void updateCromosoma(int index,Vm vm){
		Cromosoma cromosoma=this.cromosomaList.get(index);
		cromosoma.setVmForCromosoma(vm);
		this.cromosomaList.set(index, cromosoma);
	}
        public int findCloudlet(Cloudlet cloudlet){
            int index = -1;

            for (int i = 0; i < this.cromosomaList.size(); i++){
                if (cromosomaList.get(i).getCloudletListFromCromosoma().contains(cloudlet)){
                    index = i;
                    return index;
                }
            }

            return index;
        }
}