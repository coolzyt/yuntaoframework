package org.yuntao.framework.jvm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * <p>
 * Title:Class热替换的agent
 * </p>
 * </p>
 * <p>
 * Company: 联想研究院
 * </p>
 * 
 * @version 1.00
 * @since 2012-2-22
 * @author zhaoyuntao
 */
public class HotSwapAgent implements Runnable{
    private static Instrumentation inst;
    private String appClassRoot;
    private ClassLoader classLoader ;
    private HotSwapAgent(String appClassRoot,ClassLoader classLoader){
        this.appClassRoot = appClassRoot;
        this.classLoader = classLoader;
    }
    public static void reload(ClassDefinition... definitions) throws UnmodifiableClassException, ClassNotFoundException {
        inst.redefineClasses(definitions);
        for (ClassDefinition definition : definitions) {
            System.out.println("Reload class " + definition.getDefinitionClass().getName());
        }
    }

    private Map<String, Long> classModificationMap = new HashMap();

    private void findClass(File dir, List<File> classes) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".class") || new File(dir, name).isDirectory();
            }
        });
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                findClass(file, classes);
            } else if (file.isFile()) {
                classes.add(file);
            }
        }

    }

    private String getClassName(File classFile, File rootDir) {
        String nameWithSuffix = classFile.getPath().substring(rootDir.getPath().length()+1)
                .replaceAll("[/\\\\]", ".");
        return nameWithSuffix.substring(0, nameWithSuffix.length() - 6);
    }

    private byte[] getClassBytes(File classFile) throws IOException {
        FileInputStream fis = new FileInputStream(classFile);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int p = 0;
        while ((p = fis.read(b)) != -1) {
            os.write(b, 0, p);
        }
        fis.close();
        os.flush();
        return os.toByteArray();
    }

    @Override
    public void run() {
        File root = new File(appClassRoot);
        if (!root.exists() && root.isFile()) {
            return;
        }
        boolean first = true;
        while (true) {
            try {
                ClassLoader clsLoader = classLoader;
                List<File> list = new ArrayList();
                findClass(root, list);
                List<ClassDefinition> classes = new ArrayList();
                for (File f : list) {
                    String className = getClassName(f, root);
                    if(first){
                        classModificationMap.put(className, f.lastModified());
                        continue;
                    }
                    Long lastModified = classModificationMap.get(className);
                    if (lastModified != null && lastModified == f.lastModified()) {
                        continue;
                    }
                    byte[] classBytes = getClassBytes(f);
                    Class cls = clsLoader.loadClass(className);
                    ClassDefinition classDefinition = new ClassDefinition(cls, classBytes);
                    classes.add(classDefinition);
                    classModificationMap.put(className, f.lastModified());
                }
                first = false;
                reload(classes.toArray(new ClassDefinition[classes.size()]));
            } catch (Exception e) {
                System.err.println("加载class发生异常");
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    

    public static String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    public static void startHotswap(String appClassRoot,ClassLoader classLoader) throws AttachNotSupportedException, IOException,
            AgentLoadException, AgentInitializationException {
        List<VirtualMachineDescriptor> vmds = VirtualMachine.list();
        String pid = getCurrentPid();
        for (VirtualMachineDescriptor vmd : vmds) {
            if (vmd.id().equals(pid)) {
                VirtualMachine vm = VirtualMachine.attach(vmd);
                vm.loadAgent(createAgentJar());
                vm.detach();
                break;
            }
        }
        new Thread(new HotSwapAgent(appClassRoot,classLoader)).start();
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException,
            UnmodifiableClassException, InterruptedException {
        HotSwapAgent.inst = inst;
    }

    private static String createAgentJar() throws IOException {
        File file = File.createTempFile("yuntaoframework-hotswap", ".jar");
        file.deleteOnExit();
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
        zout.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(zout));
        writer.println("Agent-Class: " + HotSwapAgent.class.getName());
        writer.println("Can-Redefine-Classes: true");
        writer.println("Can-Retransform-Classes: true");
        writer.close();
        return file.getAbsolutePath();
    }

}
