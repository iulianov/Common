package aohara.common.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import aohara.common.progressDialog.ProgressDialog;

public class TestRunner {
	
	public TestRunner(){
		Downloader d = new Downloader(4, true);
		d.addListener(new ProgressDialog<Path>("Download Progress"));
		
		try {
			d.download(new URL("http://download.tuxfamily.org/notepadplus/6.6.7/npp.6.6.7.Installer.exe"), Paths.get("foo.zip"));
			//d.download(new URL("http://www.dotpdn.com/files/paint.net.4.0.install.zip"), Paths.get("foo.zip"));
			d.download(new URL("http://www.dotpdn.com/files/paint.net.4.0.install.zip"), Paths.get("bar.zip"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new TestRunner();
	}
}
