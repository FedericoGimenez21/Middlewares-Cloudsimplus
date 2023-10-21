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
public class Individuo {
    	private ArrayList<Cloudlet> cloudletList;
	private Vm vm;
	public Individuo(ArrayList<Cloudlet> cl, Vm v)
	{

		this.cloudletList=cl;
		this.vm=v;
	}
	public ArrayList<Cloudlet> getCloudletListFromIndividuo()
	{
		return this.cloudletList;
	}
	public Vm getVmFromaIndividuo()
	{
		return this.vm;
	}
	public void addCloudletForIndividuo(Cloudlet cl)
	{
		this.cloudletList.add(cl);
	}
	public void setVmForIndividuo(Vm vm)
	{
		this.vm=vm;
	}
}