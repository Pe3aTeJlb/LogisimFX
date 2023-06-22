package LogisimFX.fpga;

import LogisimFX.file.LogisimFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FPGAToolchainOrchestratorData {

	private FPGAToolchainOrchestrator orchestrator;
	private boolean isDefault = true;

	private String dockerImg;
	private String boardName;
	private boolean generateConstrains;
	private double freq, div;
	private boolean generateTopLevel;

	public FPGAToolchainOrchestratorData(){ }

	public void registerOrchestrator(FPGAToolchainOrchestrator orchestrator){
		this.orchestrator = orchestrator;
	}

	public Element getData(Document doc){
		return orchestrator.getOrchestratorData(doc);
	}

	public boolean isDefault(){
		return isDefault;
	}

	public void setDockerImg(String img){
		dockerImg = img;
		isDefault = false;
	}

	public String getDockerImg(){
		return dockerImg;
	}

	public void setBoardName(String board){
		boardName = board;
		isDefault = false;
	}

	public String getBoardName(){
		return boardName;
	}

	public void setGenerateConstrains(boolean b){
		generateConstrains = b;
		isDefault = false;
	}

	public boolean isGenerateConstrains(){
		return generateConstrains;
	}

	public void setFreq(double d){
		freq = d;
		isDefault = false;
	}

	public double getFreq(){
		return freq;
	}

	public void setDiv(double d){
		div = d;
		isDefault = false;
	}

	public double getDiv() {
		return div;
	}

	public void setGenerateTopLevel(boolean b){
		generateTopLevel = b;
		isDefault = false;
	}

	public boolean isGenerateTopLevel(){
		return generateTopLevel;
	}

	public void copyFrom(FPGAToolchainOrchestratorData other, LogisimFile file) {
		this.dockerImg = other.dockerImg;
		this.boardName = other.boardName;
		this.generateConstrains = other.generateConstrains;
		this.freq = other.freq;
		this.div = other.div;
		this.generateTopLevel = other.generateTopLevel;
	}

}
