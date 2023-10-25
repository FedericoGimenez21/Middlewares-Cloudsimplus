/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

import java.util.ArrayList;
import org.cloudsimplus.vms.Vm;


/**
 *
 * @author FD_gi
 */
public class Individuo{
	
	protected ArrayList<Cromosoma> cromosomaList;

	
	public Individuo(ArrayList<Cromosoma> individuosList){
		this.cromosomaList=individuosList;		
	}
	
	public ArrayList<Cromosoma> getCromosomasList(){
		return this.cromosomaList;
	}
	
	public void updateCromosoma(int index,Vm vm){
		Cromosoma individuo=this.cromosomaList.get(index);
		individuo.setVmForCromosoma(vm);
		this.cromosomaList.set(index, individuo);
	}
        

}