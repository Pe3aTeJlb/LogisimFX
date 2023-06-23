package LogisimFX.util;

import LogisimFX.file.Loader;
import LogisimFX.file.LogisimFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	public static void zipFolder(Path sourceDirectoryPath, Path zipPath) throws IOException {
		Path zipFilePath;
		if (!zipPath.toFile().exists()) {
			zipFilePath = Files.createFile(zipPath);
		} else {
			zipFilePath = zipPath;
		}

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {

			Files.walk(sourceDirectoryPath).filter(path -> !Files.isDirectory(path))
					.forEach(path -> {
						ZipEntry zipEntry = new ZipEntry(sourceDirectoryPath.relativize(path).toString());
						try {
							zipOutputStream.putNextEntry(zipEntry);
							zipOutputStream.write(Files.readAllBytes(path));
							zipOutputStream.closeEntry();
						} catch (Exception e) {
							System.err.println(e);
						}
					});
		}
	}

	public static void unzipFolder(Path zipFilePath, Path unzipLocation) throws IOException {

		if (!(Files.exists(unzipLocation))) {
			Files.createDirectories(unzipLocation);
		}

		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				Path filePath = Paths.get(unzipLocation.toString(), entry.getName());
				if (!entry.isDirectory()) {
					filePath.toFile().getParentFile().mkdirs();
					unzipFiles(zipInputStream, filePath);
				} else {
					Files.createDirectories(filePath);
				}

				zipInputStream.closeEntry();
				entry = zipInputStream.getNextEntry();
			}
		}

	}

	public static void unzipFiles(final ZipInputStream zipInputStream, final Path unzipFilePath) throws IOException {

		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
			byte[] bytesIn = new byte[1024];
			int read = 0;
			while ((read = zipInputStream.read(bytesIn)) != -1) {
				bos.write(bytesIn, 0, read);
			}
		}

	}

	public static void unzipProject(Path zipFilePath, Path unzipLocation) throws IOException {

		if (!(Files.exists(unzipLocation))) {
			Files.createDirectories(unzipLocation);
		}

		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				Path filePath = Paths.get(unzipLocation.toString(), entry.getName());
				if (!entry.isDirectory()) {
					if (entry.getName().endsWith(Loader.LOGISIM_PROJ_DESC) ||
						entry.getName().contains(LogisimFile.CIRCUIT+File.separator) ||
						entry.getName().contains(LogisimFile.LIB+File.separator)){
						if (entry.getName().endsWith(Loader.LOGISIM_PROJ_DESC)) {
							filePath = Paths.get(unzipLocation + File.separator + LogisimFile.LIB + File.separator + entry.getName());
						}
						filePath.toFile().getParentFile().mkdirs();
						unzipFiles(zipInputStream, filePath);
					}
				} else {
					Files.createDirectories(filePath);
				}

				zipInputStream.closeEntry();
				entry = zipInputStream.getNextEntry();
			}
		}

	}



	public static boolean isZip(File f) {
		int fileSignature = 0;
		try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			// handle if you like
		}
		return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
	}


}
