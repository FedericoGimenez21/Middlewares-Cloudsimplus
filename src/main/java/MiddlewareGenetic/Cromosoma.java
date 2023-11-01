/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

/**
 *
 * @author FD_gi
 */
import java.util.ArrayList;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.cloudlets.Cloudlet;
/**
 *
 * @author FD_gi
 */
public class Cromosoma {
    	private ArrayList<Cloudlet> cloudletList;
	private Vm vm;
	public Cromosoma(ArrayList<Cloudlet> cl, Vm v)
	{

		this.cloudletList=cl;
		this.vm=v;
	}
        
        public Cromosoma(Vm vm){
            this.vm = vm;
            this.cloudletList = new ArrayList<Cloudlet>();
        }
        
	public ArrayList<Cloudlet> getCloudletListFromCromosoma()
	{
		return this.cloudletList;
	}
	public Vm getVmFromCromosoma()
	{
		return this.vm;
	}
	public void addCloudletForCromosoma(Cloudlet cl)
	{
		this.cloudletList.add(cl);
	}
	public void setVmForCromosoma(Vm vm)
	{
		this.vm=vm;
	}
}