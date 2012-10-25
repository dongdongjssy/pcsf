/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.crt.test;

import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.SAVE_PATH;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author ddong
 * @version 0.1
 * @createDate Jul 16, 2012
 * @email dong.dong@unb.ca
 */
public class CreationServiceTest {
	public void zipFile(String name) {
		try {
			// source folder
			String filePath = "c:/dongdong";
			File[] files = (new File(filePath)).listFiles();

			// temp folder
			String destPath = SAVE_PATH + File.separator + "tmp";
			File destFolder = new File(destPath);
			if (!destFolder.exists() || !destFolder.isDirectory())
				destFolder.mkdir();

			// zip file
			File zipFile = new File("c:/dongdong/tmpZip.zip");
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));

			// compress files
			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						System.out.println("adding file <" + file.getName() + "> into zip file...");
						String newFileName = destPath + File.separator + name + file.getName();
						File newFile = new File(newFileName);
						copyFile(file, newFile);

						FileInputStream inputStream = new FileInputStream(newFile);
						zipOutputStream.putNextEntry(new ZipEntry(newFile.getName()));
						int len;
						while ((len = inputStream.read()) != -1) {
							zipOutputStream.write(len);
						}

						inputStream.close();
					}
				}
			}

			System.out.println("deleting temp files...");
			delDirectory(destFolder);
			zipOutputStream.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void copyFile(File sourceFile, File targetFile) throws IOException {
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		outBuff.flush();

		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	private void delDirectory(File directory) {
		if (directory.exists()) {
			File[] children = directory.listFiles();
			if (children == null || children.length <= 0)
				directory.delete();
			else {
				for (File child : children) {
					if (child.exists() && child.isFile())
						child.delete();
					if (child.exists() && child.isDirectory())
						delDirectory(child);
				}
				directory.delete();
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CreationServiceTest().zipFile("tmp-");
	}

	public static void generateConf(String name) throws IOException {

		String serviceFolderPath = SAVE_PATH + File.separator + name;

		File serviceFolder = new File(serviceFolderPath);
		File[] services = serviceFolder.listFiles();

		for (File service : services) {
			if (service.isDirectory()) {
				String servicePath = service.getAbsolutePath();
				String confFileName = name + "-" + service.getName() + ".xml";

				String content = "<Context docBase=\"" + servicePath + "\" reloadable=\"true\"/>";

				File confFile = new File(serviceFolderPath + File.separator + confFileName);
				if (!confFile.exists())
					confFile.createNewFile();

				PrintWriter printWriter = new PrintWriter(new FileWriter(confFile));
				printWriter.write(content);
				printWriter.close();
			}
		}

	}

}
