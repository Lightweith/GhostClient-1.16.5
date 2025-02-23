//package com.example.mod.utils;
//
//import com.sun.jna.Native;
//import com.sun.jna.Pointer;
//import com.sun.jna.Structure;
//import com.sun.jna.platform.win32.WinDef;
//import com.sun.jna.ptr.IntByReference;
//import com.sun.jna.win32.StdCallLibrary;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Random;
//
//public class Cleaner {
//
//    private static boolean working;
//    private static int percentage = 0;
//    private static int found = 0;
//
//    private static final int MEM_COMMIT = 0x00001000;
//    private static final int PAGE_READWRITE = 0x04;
//    private static final int PAGE_EXECUTE_READWRITE = 0x40;
//    private static final int PAGE_GUARD = 0x100;
//    private static final int PAGE_NOACCESS = 0x01;
//
//    public interface Kernel32 extends StdCallLibrary {
//        Kernel32 INSTANCE = Native.loadLibrary("kernel32", Kernel32.class);
//
//        int PROCESS_QUERY_INFORMATION = 0x0400;
//        int MEM_COMMIT = 0x00001000;
//        int PAGE_READWRITE = 0x04;
//        int PROCESS_WM_READ = 0x0010;
//        int MEM_FREE = 0x00010000;
//        int PAGE_EXECUTE_READWRITE = 0x40;
//        int PAGE_GUARD = 0x100;
//        int PAGE_NOACCESS = 0x01;
//
//        int PROCESS_ALL_ACCESS = 0x1F0FFF;
//
//        Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
//
//        boolean ReadProcessMemory(Pointer hProcess, long lpBaseAddress, byte[] lpBuffer, int nSize, IntByReference lpNumberOfBytesRead);
//
//        void GetSystemInfo(SYSTEM_INFO lpSystemInfo);
//
//        int VirtualQueryEx(Pointer hProcess, long lpAddress, MEMORY_BASIC_INFORMATION64 lpBuffer, int dwLength);
//
//        boolean WriteProcessMemory(Pointer hProcess, long lpBaseAddress, byte[] lpBuffer, int nSize, IntByReference lpNumberOfBytesWritten);
//
//        boolean VirtualProtectEx(Pointer hProcess, long lpAddress, long dwSize, int newProtect, IntByReference oldProtect);
//
//
//
//        public static class SYSTEM_INFO extends Structure {
//            public WinDef.WORD processorArchitecture;
//            public WinDef.WORD reserved;
//            public WinDef.DWORD pageSize;
//            public Pointer minimumApplicationAddress;
//            public Pointer maximumApplicationAddress;
//            public Pointer activeProcessorMask;
//            public WinDef.DWORD numberOfProcessors;
//            public WinDef.DWORD processorType;
//            public WinDef.DWORD allocationGranularity;
//            public WinDef.WORD processorLevel;
//            public WinDef.WORD processorRevision;
//
//            @Override
//            protected List<String> getFieldOrder() {
//                return Arrays.asList(
//                        "processorArchitecture", "reserved", "pageSize",
//                        "minimumApplicationAddress", "maximumApplicationAddress",
//                        "activeProcessorMask", "numberOfProcessors",
//                        "processorType", "allocationGranularity",
//                        "processorLevel", "processorRevision"
//                );
//            }
//        }
//
//        class MEMORY_BASIC_INFORMATION64 extends Structure {
//            public long BaseAddress;
//            public long AllocationBase;
//            public int AllocationProtect;
//            public int __alignment1;
//            public long RegionSize;
//            public int State;
//            public int Protect;
//            public int Type;
//            public int __alignment2;
//
//            @Override
//            protected List<String> getFieldOrder() {
//                return Arrays.asList(
//                        "BaseAddress", "AllocationBase", "AllocationProtect",
//                        "__alignment1", "RegionSize", "State", "Protect",
//                        "Type", "__alignment2"
//                );
//            }
//        }
//    }
//    public static void clean(UnsafeString pid, List<String> strings) {
//        working = true;
//        new Thread(() -> {
//            while (working) {
//                System.out.print("\r[ \u001b[34m*\u001b[37m ] Отчистка строк... / (" + Cleaner.percentage + ")");
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.print("\r[ \u001b[34m*\u001b[37m ] Отчистка строк... - (" + Cleaner.percentage + ")");
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.print("\r[ \u001b[34m*\u001b[37m ] Отчистка строк... \\ (" + Cleaner.percentage + ")");
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        Kernel32.SYSTEM_INFO sys_info = new Kernel32.SYSTEM_INFO();
//        Kernel32.INSTANCE.GetSystemInfo(sys_info);
//        System.gc();
//        Pointer proc_min_address = sys_info.minimumApplicationAddress;
//        Pointer proc_max_address = sys_info.maximumApplicationAddress;
//
//
//        long proc_min_address_l = Pointer.nativeValue(proc_min_address);
//        long proc_max_address_l = Pointer.nativeValue(proc_max_address);
//
//
//
//        Pointer processHandle = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_ALL_ACCESS, false,Integer.parseInt(pid.toString()));
//
//
//        Kernel32.MEMORY_BASIC_INFORMATION64 mem_basic_info = new Kernel32.MEMORY_BASIC_INFORMATION64();
//        IntByReference bytesRead = new IntByReference(0);
//
//        while (proc_min_address_l < proc_max_address_l) {
//            System.gc();
//            Kernel32.INSTANCE.VirtualQueryEx(processHandle, proc_min_address_l, mem_basic_info, mem_basic_info.size());
//
//            if ((mem_basic_info.Type & Kernel32.MEM_FREE) != 0) continue;
//
//            if ((mem_basic_info.Protect & Kernel32.PAGE_READWRITE) == 0) {
//                if ((mem_basic_info.Protect & (Kernel32.PAGE_GUARD | Kernel32.PAGE_NOACCESS)) != 0) {
//                    Kernel32.INSTANCE.VirtualProtectEx(processHandle, mem_basic_info.BaseAddress, mem_basic_info.RegionSize, Kernel32.PAGE_EXECUTE_READWRITE, new IntByReference());
//                }
//            }
//            try {
//                if ((mem_basic_info.Protect & Kernel32.PAGE_READWRITE) != 0 &&
//                        mem_basic_info.State == Kernel32.MEM_COMMIT && mem_basic_info.Type != Kernel32.MEM_FREE) {
//                    byte[] buffer;
//                    try {
//                        buffer = new byte[(int) mem_basic_info.RegionSize];
//                    }catch (NegativeArraySizeException e){
//                        continue;
//                    }
//
//                    Kernel32.INSTANCE.ReadProcessMemory(processHandle, mem_basic_info.BaseAddress, buffer, (int) mem_basic_info.RegionSize, bytesRead);
//
//                    for (String x : strings) {
//                        int length = x.length();
//                        long size = mem_basic_info.RegionSize;
//                        for (long i = 0; i < size; i++) {
//                            int j;
//                            for (j = 0; j < length; j++) {
//                                try {
//                                    if (buffer[(int) (i + j)] != x.charAt(j))
//                                        break;
//                                } catch (Exception ex) {
//                                    break;
//                                }
//                            }
//                            if (j == length) {
//                                byte[] replacement = GetRandomBytes(length);
//                                Kernel32.INSTANCE.WriteProcessMemory(processHandle, mem_basic_info.BaseAddress + i,
//                                        replacement, replacement.length, bytesRead);
//                                found++;
//                            }
//                        }
//                        percentage++;
//                    }
//
//                }
//            }catch (StackOverflowError e){
//                continue;
//            }
//
//
//            proc_min_address_l += mem_basic_info.RegionSize;
//            proc_min_address = new Pointer(proc_min_address_l);
//        }
//        working = false;
//
//    }
//    static HashMap<Integer, byte[]> myHashMap = new HashMap<>();
//    public static byte[] GetRandomBytes(int length)
//    {
//        if (myHashMap.containsKey(length))
//        {
//            return myHashMap.get(length);
//        }
//        else
//        {
//            byte[] bytes = new byte[length];
//            for (int i = 0; i < length; i++)
//            {
//                bytes[i] = (byte)0x00;
//            }
//            myHashMap.put(length,bytes);
//            return myHashMap.get(length);
//        }
//
//
//    }
//
//
//}