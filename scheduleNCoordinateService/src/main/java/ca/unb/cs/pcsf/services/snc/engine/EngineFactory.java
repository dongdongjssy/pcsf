/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.snc.engine;

import java.io.File;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 6, 2012
 * @email dong.dong@unb.ca
 */
public class EngineFactory {
	/**
	 * Initialize an engine by the type of the process file
	 * 
	 * @param processFile
	 * @return a process engine
	 */
	public static Engine newEngine(File processFile) {
		if (processFile.getName().endsWith(".bpmn.xml"))
			return new PcsfBpmnEngine(processFile);
		if (processFile.getName().endsWith(".jpdl.xml"))
			return new PcsfBpmnEngine(processFile);

		return null;
	}
}
