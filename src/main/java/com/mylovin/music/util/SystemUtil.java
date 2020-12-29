package com.mylovin.music.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SystemUtil {
    /**
     * 获取本机本地IP
     */
    public static String getLocalIP() {
        String localip = "";
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();
            localip = ia.getHostAddress();
            System.out.println("本机的ip是 ：" + localip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localip;
    }

    /**
     * 获取本地真正的IP地址，即获得有线或者无线WiFi地址（真实物理网卡IP）。
     * 过滤虚拟机、蓝牙等地址
     */
    public static String getRealIP() {
        try {
            //获取到所有的网卡
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                // 去除回环接口127.0.0.1，子接口，未运行的接口
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }
                //获取名称中是否包含 Intel Realtek 的网卡
                if (!netInterface.getDisplayName().contains("Intel")
                        && !netInterface.getDisplayName().contains("Realtek")
                        && !netInterface.getDisplayName().contains("Atheros")
                        && !netInterface.getDisplayName().contains("Broadcom")) {
                    continue;
                }
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                System.out.println(netInterface.getDisplayName());
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null) {
                        if (ip instanceof Inet4Address) {
                            System.out.println(ip.getHostName());
                            return ip.getHostAddress();
                        }
                    }
                }
                break;
            }
        } catch (SocketException e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * 获取到所有的在活动的网卡IP 包含虚拟网卡
     *
     * @return
     */
    static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        System.out.println(inetAddress.getHostName());
                        ip = inetAddress.getHostAddress();
                        System.out.println(ip);
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }
}
