package LogisimFX.newgui.MainFrame;

import LogisimFX.file.LogisimFile;
import LogisimFX.proj.Project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class FrameLayout {

    private Project proj;

    private MainWindowDescriptor mainWindowDescriptor;
    private ArrayList<SubWindowDescriptor> subWindowDescriptors = new ArrayList<>();

    private boolean defaultLayout = true;

    public static class MainWindowDescriptor {

        public double width, height, x, y;
        public boolean isFullScreen;
        public ArrayList<SideBarDescriptor> sideBarDescriptors = new ArrayList<>();
        public ArrayList<TabPaneLayoutDescriptor> tabPaneLayoutDescriptors = new ArrayList<>();

        public MainWindowDescriptor(double width, double height, double x, double y, boolean isFullScreen){
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.isFullScreen = isFullScreen;
        }

        public void addSystemSideBarDescriptor(SideBarDescriptor descriptor){
            sideBarDescriptors.add(descriptor);
        }

        public void addTabPaneDescriptor(TabPaneLayoutDescriptor descriptor){
            tabPaneLayoutDescriptors.add(descriptor);
        }

    }

    public static class SideBarDescriptor {

        public String name;
        public boolean leftCollapsed, rightCollapsed;
        public double size;
        public ArrayList<SystemTabDescriptor> systemTabDescriptors = new ArrayList<>();

        public SideBarDescriptor(String name, boolean leftCollapsed, boolean rightCollapsed,double size){
            this.name = name;
            this.leftCollapsed = leftCollapsed;
            this.rightCollapsed = rightCollapsed;
            this.size = size;
        }

        public void addSystemTabDescriptor(SystemTabDescriptor descriptor){
            systemTabDescriptors.add(descriptor);
        }

    }

    public static class SystemTabDescriptor {
        public String side, type;
        public SystemTabDescriptor(String side, String type){
            this.side = side;
            this.type = type;
        }
    }

    public static class EditorTabDescriptor {
        public String circ, type;
        public boolean isSelected;
        public EditorTabDescriptor(String circ, String type, boolean isSelected){
            this.circ = circ;
            this.type = type;
            this.isSelected = isSelected;
        }
    }

    public static class TabPaneLayoutDescriptor {

        public String anchor;
        public boolean append;
        public ArrayList<EditorTabDescriptor> tabs = new ArrayList<>();

        public TabPaneLayoutDescriptor(String anchor, boolean append){
            this.anchor = anchor;
            this.append = append;
        }

        public void addTabDescriptor(EditorTabDescriptor descriptor){
            tabs.add(descriptor);
        }

    }


    public static class SubWindowDescriptor {

        public double width, height, x, y;
        public boolean isFullScreen;
        public ArrayList<TabPaneLayoutDescriptor> tabpanes = new ArrayList<>();

        public SubWindowDescriptor(double width, double height, double x, double y, boolean isFullScreen){
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.isFullScreen = isFullScreen;
        }

        public void addTabPaneDescriptor(TabPaneLayoutDescriptor descriptor){
            tabpanes.add(descriptor);
        }

    }


    public FrameLayout(){ }


    public void registerProject(Project project){
        proj = project;
    }

    public boolean isLayoutDefault(){
        return defaultLayout;
    }


    public void setMainWindowDescriptor(MainWindowDescriptor descriptor){
        defaultLayout = false;
        mainWindowDescriptor = descriptor;
    }

    public MainWindowDescriptor getMainWindowDescriptor(){
        return mainWindowDescriptor;
    }


    public void addSubWindowDescriptor(SubWindowDescriptor descriptor){
        defaultLayout = false;
        subWindowDescriptors.add(descriptor);
    }

    public ArrayList<SubWindowDescriptor> getSubWindowDescriptors(){
        return subWindowDescriptors;
    }



    public Element getLayout(Document doc){
        return proj.getFrameController().getLayout(doc);
    }

    public void copyFrom(FrameLayout other, LogisimFile file) {
        /*
        if (this == other) return;
        cache_mods = -1;
        this.map.clear();
        for (Integer mods : other.map.keySet()) {
            Tool srcTool = other.map.get(mods);
            Tool dstTool = file.findTool(srcTool);
            if (dstTool != null) {
                dstTool = dstTool.cloneTool();
                AttributeSets.copy(srcTool.getAttributeSet(),
                        dstTool.getAttributeSet());
                this.map.put(mods, dstTool);
            }
        }
        fireMouseMappingsChanged();
         */
    }

}
