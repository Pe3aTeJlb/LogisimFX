package LogisimFX.fpga;

import LogisimFX.file.LogisimFile;
import LogisimFX.proj.Project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class SerializedFilesContainer {

	private Project proj;
	private ArrayList<SerializedFile> serializedFiles = new ArrayList<>();

	public SerializedFilesContainer() { }


	public static class SerializedFile {

		public String path;
		public String data;

		public SerializedFile(String path, String data){
			this.path = path;
			this.data = data;
		}

	}

	public void addSerializedData(SerializedFile data){
		serializedFiles.add(data);
	}

	public ArrayList<SerializedFile> getSerializedFiles(){
		return serializedFiles;
	}


	public void registerProject(Project project){
		proj = project;
	}

	public Element getSerializedFiles(Document doc){
		return proj.getFpgaToolchainOrchestrator().getSerializedFiles(doc);
	}

	public void copyFrom(SerializedFilesContainer other, LogisimFile file) {

		other.proj = proj;
		other.serializedFiles = new ArrayList<>(serializedFiles);

	}

}
