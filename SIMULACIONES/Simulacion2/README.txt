La arquitectura general es la siguiente (SE INCREMENTA LA CANTD DE CLOUDLETS CON RESPECTO A SIMULACION1) 
y en los diferentes test se ha ido incrementando la cantidad de Vms para comprobar la escalabilidad y comportamiento:    
    private static final int  HOSTS = 100;
    private static final int  HOST_PES = 32;
    private static final int  HOST_MIPS = 10000; // Milion Instructions per Second (MIPS)
    private static final int  HOST_RAM = 65_536; //in Megabytes
    private static final long HOST_BW = 10_000; //in Megabits/s
    private static final long HOST_STORAGE = 1_000_000; //in Megabytes

    private static final int VMS = 50;
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
    
    
    
    private static final int CLOUDLETS = 100;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 100_000; // Milion Instructions (MI)
    private static final int CLOUDLET_SIZE=1024;            
    private static final int CLOUDLET_FileSize=1024;         
    private static final int CLOUDLET_OutputSize=1024;   