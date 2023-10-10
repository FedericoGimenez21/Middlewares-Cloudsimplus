/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MiddlewareGenetic;

/**
 *
 * @author FD_gi
 */
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.cloudlets.Cloudlet;
/**
 *
 * @author FD_gi
 */
public class Individuo {
    	private Cloudlet task;
	private Vm vm;
	public Individuo(Cloudlet cl, Vm v)
	{
		this.task=cl;
		this.vm=v;
	}
	public Cloudlet getCloudletFromIndividuo()
	{
		return this.task;
	}
	public Vm getVmFromaIndividuo()
	{
		return this.vm;
	}
	public void setCloudletForIndividuo(Cloudlet cl)
	{
		this.task=cl;
	}
	public void setVmForIndividuo(Vm vm)
	{
		this.vm=vm;
	}
}