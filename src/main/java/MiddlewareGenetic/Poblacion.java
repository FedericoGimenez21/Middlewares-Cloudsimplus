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
public class Poblacion{
	
	protected ArrayList<Individuo> individuosList;

	
	public Poblacion(ArrayList<Individuo> individuosList){
		this.individuosList=individuosList;		
	}
	
	public ArrayList<Individuo> getIndividuosList(){
		return this.individuosList;
	}
	
	public void updateIndividuo(int index,Vm vm){
		Individuo individuo=this.individuosList.get(index);
		individuo.setVmForIndividuo(vm);
		this.individuosList.set(index, individuo);
	}
        

}