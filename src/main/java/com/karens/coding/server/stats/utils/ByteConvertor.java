package com.karens.coding.server.stats.utils;

/**
 * 
 * @author karen
 *
 */
public class ByteConvertor {

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static Double convertBytesToKilobytes(Long bytes) {
		Double kilobytes = 0.0;
		if (bytes != null) {
			kilobytes = bytes/1024.00;
		}
		return kilobytes;
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static Double convertBytesToMegabytes(Long bytes) {
		Double megabytes = 0.0;
		if (bytes != null) {
			megabytes = bytes/1048576.00;
		}
		return megabytes;
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static Double convertBytesToGigabytes(Long bytes) {
		Double gigabytes = 0.0;
		if (bytes != null) {
			gigabytes = bytes/1073741824.00;
		}
		return gigabytes;
	}
	
	/**
	 * 
	 * @param bytes
	 * @param unit
	 * @return
	 */
	public static Double convertBytesByType(Long bytes, String unit) {
		Double convertedFreeSpace = Double.longBitsToDouble(bytes);
		if (unit.equalsIgnoreCase("kb")) {
			convertedFreeSpace = ByteConvertor.convertBytesToKilobytes(bytes);
		}
		else if (unit.equalsIgnoreCase("mb")) {
			convertedFreeSpace = ByteConvertor.convertBytesToMegabytes(bytes);
		}
		else if (unit.equalsIgnoreCase("gb")) {
			convertedFreeSpace = ByteConvertor.convertBytesToGigabytes(bytes);
		}	
		return convertedFreeSpace;
	}
}
