package com.nightox.q.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import com.nightox.q.api.ApiException;
import com.nightox.q.beans.Factory;

public class FileUtils {
	
	public static void createFile(String path, InputStream contents) throws ApiException
	{
		// make sure folder exists
		String			folder = new File(path).getParentFile().getAbsolutePath();
		String			cmd =  Factory.getInstanceEnvironment().getShellCommand("mkdir") + " -p " + folder;
		ExecUtils.executeShellCatch(cmd, null);
		
		cmd =  Factory.getInstanceEnvironment().getShellCommand("tee") + " " + path;
		ExecUtils.executeShellCatch(cmd, contents);
	}
	
	public static void createFileFromResource(String path, String resource) throws ApiException
	{
		InputStream is;
		try {
			is = (new ClassPathResource(resource)).getInputStream();
		} catch (IOException e) {
			throw new ApiException(e);
		}
		
		createFile(path, is);
	}

}
