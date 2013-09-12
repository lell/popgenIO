package popgenIO.Core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import popgenIO.Formats.FlatFile;

import libnp.mcmc.collectors.Collectable;
import libnp.mcmc.collectors.Collector;

public class DataSetCollector implements Collector {
	private Collectable cc;
	private String property;
	private Object arg = null;
	private int index = 0;
	String filename;	

	public DataSetCollector(Collectable cc, String property, String filename, Object arg) {
		this.cc = cc;
		this.property = property;
		this.filename = filename;
		this.arg = arg;
	}

	public DataSetCollector(Collectable cc, String property, String prefix) {
		this.cc = cc;
		this.property = property;
		this.filename = filename;
	}

	@Override
	public void collect() {
		Object returned = null;
		if (arg != null) {
			returned = cc.get(property, arg);
		} else {
			returned = cc.get(property);
		}
		assert returned != null : property;
		FlatFile.write((DataSet<Boolean>)returned, filename);
	}

	@Override
	public void close() {
	}
}
