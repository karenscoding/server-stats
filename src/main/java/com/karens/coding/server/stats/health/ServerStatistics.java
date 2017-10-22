package com.karens.coding.server.stats.health;

import java.io.File;
import java.io.IOException;
import java.lang.management.*;
import java.lang.reflect.Method;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.karens.coding.server.stats.utils.ByteConvertor;
import java.lang.management.OperatingSystemMXBean;

/**
 * Provides data and statistics about the current operating system
 * @author karen
 *
 */
public class ServerStatistics {
	
	/** Logger */
	private static final Logger logger = LogManager.getLogger(ServerStatistics.class);
	
	/** Free space for the current working directory in bytes */
	private Long freeSpace = 0l;
	
	/** Total space for the current working directory in bytes */
	private Long totalSpace = 0l;
	
	/** Usable space for the current working directory in bytes */
	private Long usableSpace = 0l;
	
	// Converted values for json conversions
	
	/** Free space for the current working directory in KB */
	private Double freeSpaceAsKilobytes = 0.0;
	/** Total space for the current working directory in KB */
	private Double totalSpaceAsKilobytes = 0.0;
	/** Usable space for the current working directory in KB */
	private Double usableSpaceAsKilobytes = 0.0;
	
	/** Free space for the current working directory in MB */
	private Double freeSpaceAsMegabytes = 0.0;
	/** Total space for the current working directory in MB */
	private Double totalSpaceAsMegabytes = 0.0;
	/** Usable space for the current working directory in MB */
	private Double usableSpaceAsMegabytes = 0.0;
	
	/** Free space for the current working directory in GB */
	private Double freeSpaceAsGigabytes = 0.0;
	/** Total space for the current working directory in GB */
	private Double totalSpaceAsGigabytes = 0.0;
	/** Usable space for the current working directory in GB */
	private Double usableSpaceAsGigabytes = 0.0;
	
	/** System CPU usage */
	private Double systemCpuUsage = 0.0;
	/** JVM CPU usage */
	private Double jvmCpuUsage = 0.0;
	/** Process CPU usage */
	private Double processCpuUsage = 0.0;
	
	/** The amount of virtual memory that is guaranteed to be available to the running process in bytes, or -1 if this operation is not supported */
	private Long committedVirtualMemorySize;
	  
	/** The total amount of swap space in bytes. */
	private Long totalSwapSpaceSize;
	  
	/** The amount of free swap space in bytes */
	private Long freeSwapSpaceSize;
	  
	/** The CPU time used by the process on which the Java virtual machine is running in nanoseconds. */
	private Long processCpuTime;
	  
	/** The amount of free physical memory in bytes. */
	private Long freePhysicalMemorySize;
	  
	/** The total amount of physical memory in bytes. */
	private Long totalPhysicalMemorySize;
	  
	/** The "recent cpu usage" for the whole system. This value is a double in the [0.0,1.0] interval. */
	private Double systemCpuLoad;
	  
	/** The "recent cpu usage" for the Java Virtual Machine process. This value is a double in the [0.0,1.0] interval */
	private Double processCpuLoad;
	
	/** Map of FileStore name to FileStore object */
	private Map<String, FileStore> store = new HashMap<String, FileStore>();
	
	
	public ServerStatistics() {
		init();
	}
	
	public void init() {
		getFileSystemStatistics();
		getOperatingSystemStatistics();
	}

	protected void getOperatingSystemStatistics() {
		try {
			OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			
			committedVirtualMemorySize = (Long) callOperatingSystemMXBeanMethod(osBean,"getCommittedVirtualMemorySize");
			  
			totalSwapSpaceSize = (Long) callOperatingSystemMXBeanMethod(osBean,"getTotalSwapSpaceSize");
			  
			freeSwapSpaceSize = (Long) callOperatingSystemMXBeanMethod(osBean,"getFreeSwapSpaceSize");
			  
			processCpuTime = (Long) callOperatingSystemMXBeanMethod(osBean,"getProcessCpuTime");
			  
			freePhysicalMemorySize = (Long) callOperatingSystemMXBeanMethod(osBean,"getFreePhysicalMemorySize");
			  
			totalPhysicalMemorySize = (Long) callOperatingSystemMXBeanMethod(osBean,"getTotalPhysicalMemorySize");
			  
			systemCpuLoad = (Double) callOperatingSystemMXBeanMethod(osBean,"getSystemCpuLoad");
			  
			processCpuLoad = (Double) callOperatingSystemMXBeanMethod(osBean,"getProcessCpuLoad");
			
		} catch (Exception e) {
			logger.error("Unable to get server statistics " + e.getMessage());
		}
	}
	
	protected Object callOperatingSystemMXBeanMethod(OperatingSystemMXBean osBean, String methodName) {
		Object result = null;
		try {
		    if (Class.forName("java.lang.management.OperatingSystemMXBean").isInstance(osBean)) {
		        Method osBeanMethod = osBean.getClass().getDeclaredMethod(methodName);
		        osBeanMethod.setAccessible(true);
		        result = osBeanMethod.invoke(osBean);
		    }
		} catch (Exception e) {
			logger.error("Unable to call OperatingSystemMXBean method " + e.getMessage());
		}
		return result;
	}

	protected void getFileSystemStatistics() {
		try {
			File file = new File(".");
			freeSpace = file.getFreeSpace();
			totalSpace = file.getTotalSpace();
			usableSpace = file.getUsableSpace();
			
			
			freeSpaceAsKilobytes = ByteConvertor.convertBytesToKilobytes(freeSpace);
			totalSpaceAsKilobytes = ByteConvertor.convertBytesToKilobytes(totalSpace);
			usableSpaceAsKilobytes = ByteConvertor.convertBytesToKilobytes(usableSpace);
			
			freeSpaceAsMegabytes = ByteConvertor.convertBytesToMegabytes(freeSpace);
			totalSpaceAsMegabytes = ByteConvertor.convertBytesToMegabytes(totalSpace);
			usableSpaceAsMegabytes = ByteConvertor.convertBytesToMegabytes(usableSpace);
			
			freeSpaceAsGigabytes = ByteConvertor.convertBytesToGigabytes(freeSpace);
			totalSpaceAsGigabytes = ByteConvertor.convertBytesToGigabytes(totalSpace);
			usableSpaceAsGigabytes = ByteConvertor.convertBytesToGigabytes(usableSpace);
			
			systemCpuUsage = calculateSystemCpuUsage();
			jvmCpuUsage = calculateJvmCpuUsage();
			
			for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
				String key = fileStore.name();
				store.put(key, fileStore);
			}
		} catch (Exception e) {
			logger.error("Unable to get server statistics " + e.getMessage());
		}
	}
	
	public double calculateSystemCpuUsage() {
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// What % load the overall system is at, from 0.0-1.0
		Double cpuload = (Double) callOperatingSystemMXBeanMethod(osBean, "getSystemCpuLoad");
		//logger.debug("System CPU " + cpuload);
		return cpuload;
	}
	
	public double calculateJvmCpuUsage() {
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean( OperatingSystemMXBean.class);
		// What % CPU load this current JVM is taking, from 0.0-1.0
		//logger.debug("Jvm CPU " + osBean.getProcessCpuLoad());
		
		return (double) callOperatingSystemMXBeanMethod(osBean, "getProcessCpuLoad");
	}
	
	public long calculateCpuUsageForProcess() {
		MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
		long nanoBefore = 0;
		long cpuBefore = 0;
		long cpuAfter = 0;
		long nanoAfter = 0;
		OperatingSystemMXBean osMBean;
		try {
			osMBean = ManagementFactory.newPlatformMXBeanProxy(
					mbsc, 
					ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
			nanoBefore = System.nanoTime();
			cpuBefore = (long) callOperatingSystemMXBeanMethod(osMBean, "getProcessCpuTime");

			// Call an expensive task, or sleep if you are monitoring a remote process
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cpuAfter = (long) callOperatingSystemMXBeanMethod(osMBean, "getProcessCpuTime");
			nanoAfter = System.nanoTime();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long percent = 0l;
		if (nanoAfter > nanoBefore) {
			percent = ((cpuAfter-cpuBefore)*100L) / (nanoAfter-nanoBefore);
		}
		
		return percent;
	}

	/**
	 * @return the freeSpace
	 */
	public Long getFreeSpace() {
		return freeSpace;
	}
	
	/**
	 * @param freeSpace the freeSpace to set
	 */
	public void setFreeSpace(Long freeSpace) {
		this.freeSpace = freeSpace;
	}

	/**
	 * @return the totalSpace
	 */
	public Long getTotalSpace() {
		return totalSpace;
	}

	/**
	 * @param totalSpace the totalSpace to set
	 */
	public void setTotalSpace(Long totalSpace) {
		this.totalSpace = totalSpace;
	}

	/**
	 * @return the usableSpace
	 */
	public Long getUsableSpace() {
		return usableSpace;
	}
	
	/**
	 * @param usableSpace the usableSpace to set
	 */
	public void setUsableSpace(Long usableSpace) {
		this.usableSpace = usableSpace;
	}

	/**
	 * @return the freeSpaceAsKilobytes
	 */
	public Double getFreeSpaceAsKilobytes() {
		return freeSpaceAsKilobytes;
	}

	/**
	 * @param freeSpaceAsKilobytes the freeSpaceAsKilobytes to set
	 */
	public void setFreeSpaceAsKilobytes(Double freeSpaceAsKilobytes) {
		this.freeSpaceAsKilobytes = freeSpaceAsKilobytes;
	}

	/**
	 * @return the totalSpaceAsKilobytes
	 */
	public Double getTotalSpaceAsKilobytes() {
		return totalSpaceAsKilobytes;
	}

	/**
	 * @param totalSpaceAsKilobytes the totalSpaceAsKilobytes to set
	 */
	public void setTotalSpaceAsKilobytes(Double totalSpaceAsKilobytes) {
		this.totalSpaceAsKilobytes = totalSpaceAsKilobytes;
	}

	/**
	 * @return the usableSpaceAsKilobytes
	 */
	public Double getUsableSpaceAsKilobytes() {
		return usableSpaceAsKilobytes;
	}

	/**
	 * @param usableSpaceAsKilobytes the usableSpaceAsKilobytes to set
	 */
	public void setUsableSpaceAsKilobytes(Double usableSpaceAsKilobytes) {
		this.usableSpaceAsKilobytes = usableSpaceAsKilobytes;
	}

	/**
	 * @return the freeSpaceAsMegabytes
	 */
	public Double getFreeSpaceAsMegabytes() {
		return freeSpaceAsMegabytes;
	}

	/**
	 * @param freeSpaceAsMegabytes the freeSpaceAsMegabytes to set
	 */
	public void setFreeSpaceAsMegabytes(Double freeSpaceAsMegabytes) {
		this.freeSpaceAsMegabytes = freeSpaceAsMegabytes;
	}

	/**
	 * @return the totalSpaceAsMegabytes
	 */
	public Double getTotalSpaceAsMegabytes() {
		return totalSpaceAsMegabytes;
	}

	/**
	 * @param totalSpaceAsMegabytes the totalSpaceAsMegabytes to set
	 */
	public void setTotalSpaceAsMegabytes(Double totalSpaceAsMegabytes) {
		this.totalSpaceAsMegabytes = totalSpaceAsMegabytes;
	}

	/**
	 * @return the usableSpaceAsMegabytes
	 */
	public Double getUsableSpaceAsMegabytes() {
		return usableSpaceAsMegabytes;
	}

	/**
	 * @param usableSpaceAsMegabytes the usableSpaceAsMegabytes to set
	 */
	public void setUsableSpaceAsMegabytes(Double usableSpaceAsMegabytes) {
		this.usableSpaceAsMegabytes = usableSpaceAsMegabytes;
	}

	/**
	 * @return the freeSpaceAsGigabytes
	 */
	public Double getFreeSpaceAsGigabytes() {
		return freeSpaceAsGigabytes;
	}

	/**
	 * @param freeSpaceAsGigabytes the freeSpaceAsGigabytes to set
	 */
	public void setFreeSpaceAsGigabytes(Double freeSpaceAsGigabytes) {
		this.freeSpaceAsGigabytes = freeSpaceAsGigabytes;
	}

	/**
	 * @return the totalSpaceAsGigabytes
	 */
	public Double getTotalSpaceAsGigabytes() {
		return totalSpaceAsGigabytes;
	}

	/**
	 * @param totalSpaceAsGigabytes the totalSpaceAsGigabytes to set
	 */
	public void setTotalSpaceAsGigabytes(Double totalSpaceAsGigabytes) {
		this.totalSpaceAsGigabytes = totalSpaceAsGigabytes;
	}

	/**
	 * @return the usableSpaceAsGigabytes
	 */
	public Double getUsableSpaceAsGigabytes() {
		return usableSpaceAsGigabytes;
	}

	/**
	 * @param usableSpaceAsGigabytes the usableSpaceAsGigabytes to set
	 */
	public void setUsableSpaceAsGigabytes(Double usableSpaceAsGigabytes) {
		this.usableSpaceAsGigabytes = usableSpaceAsGigabytes;
	}

	/**
	 * @return the systemCpuUsage
	 */
	public Double getSystemCpuUsage() {
		return systemCpuUsage;
	}

	/**
	 * @param systemCpuUsage the systemCpuUsage to set
	 */
	public void setSystemCpuUsage(Double systemCpuUsage) {
		this.systemCpuUsage = systemCpuUsage;
	}

	/**
	 * @return the jvmCpuUsage
	 */
	public Double getJvmCpuUsage() {
		return jvmCpuUsage;
	}

	/**
	 * @param jvmCpuUsage the jvmCpuUsage to set
	 */
	public void setJvmCpuUsage(Double jvmCpuUsage) {
		this.jvmCpuUsage = jvmCpuUsage;
	}

	/**
	 * @return the processCpuUsage
	 */
	public Double getProcessCpuUsage() {
		return processCpuUsage;
	}

	/**
	 * @param processCpuUsage the processCpuUsage to set
	 */
	public void setProcessCpuUsage(Double processCpuUsage) {
		this.processCpuUsage = processCpuUsage;
	}
	
	

	/**
	 * @return the committedVirtualMemorySize
	 */
	public Long getCommittedVirtualMemorySize() {
		return committedVirtualMemorySize;
	}

	/**
	 * @return the totalSwapSpaceSize
	 */
	public Long getTotalSwapSpaceSize() {
		return totalSwapSpaceSize;
	}

	/**
	 * @return the freeSwapSpaceSize
	 */
	public Long getFreeSwapSpaceSize() {
		return freeSwapSpaceSize;
	}

	/**
	 * @return the processCpuTime
	 */
	public Long getProcessCpuTime() {
		return processCpuTime;
	}

	/**
	 * @return the freePhysicalMemorySize
	 */
	public Long getFreePhysicalMemorySize() {
		return freePhysicalMemorySize;
	}

	/**
	 * @return the totalPhysicalMemorySize
	 */
	public Long getTotalPhysicalMemorySize() {
		return totalPhysicalMemorySize;
	}

	/**
	 * @return the systemCpuLoad
	 */
	public Double getSystemCpuLoad() {
		return systemCpuLoad;
	}

	/**
	 * @return the processCpuLoad
	 */
	public Double getProcessCpuLoad() {
		return processCpuLoad;
	}

	/**
	 * @return the store
	 */
	public Map<String, FileStore> getStore() {
		return store;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerStatistics [freeSpace=" + freeSpace + ", totalSpace=" + totalSpace + ", usableSpace=" + usableSpace + ", freeSpaceAsKilobytes=" + freeSpaceAsKilobytes
				+ ", totalSpaceAsKilobytes=" + totalSpaceAsKilobytes + ", usableSpaceAsKilobytes=" + usableSpaceAsKilobytes + ", freeSpaceAsMegabytes=" + freeSpaceAsMegabytes
				+ ", totalSpaceAsMegabytes=" + totalSpaceAsMegabytes + ", usableSpaceAsMegabytes=" + usableSpaceAsMegabytes + ", freeSpaceAsGigabytes=" + freeSpaceAsGigabytes
				+ ", totalSpaceAsGigabytes=" + totalSpaceAsGigabytes + ", usableSpaceAsGigabytes=" + usableSpaceAsGigabytes + ", systemCpuUsage=" + systemCpuUsage
				+ ", jvmCpuUsage=" + jvmCpuUsage + ", processCpuUsage=" + processCpuUsage + ", committedVirtualMemorySize=" + committedVirtualMemorySize + ", totalSwapSpaceSize="
				+ totalSwapSpaceSize + ", freeSwapSpaceSize=" + freeSwapSpaceSize + ", processCpuTime=" + processCpuTime + ", freePhysicalMemorySize=" + freePhysicalMemorySize
				+ ", totalPhysicalMemorySize=" + totalPhysicalMemorySize + ", systemCpuLoad=" + systemCpuLoad + ", processCpuLoad=" + processCpuLoad + ", store=" + store + "]";
	}

	

	
}
