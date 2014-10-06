package aohara.common.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import aohara.common.profiles.Profile;
import aohara.common.profiles.ProfileManager;

public class TestProfileManager {
	
	private ProfileManager mgr;
	private Path tempPath;
	
	@Before
	public void setUp() throws IOException{
		tempPath = Files.createTempFile("profiles", ".json");
		mgr = new ProfileManager(tempPath);
	}
	
	@After
	public void tearDown(){
		tempPath.toFile().delete();
	}

	@Test
	public void testAddOne() {
		String name = "foo";
		Profile p1 = mgr.newProfile(name);
		assertEquals(0, mgr.size());
		
		mgr.saveProfile(p1);
		assertEquals(1, mgr.size());
		assertEquals(p1, mgr.getProfile(name));
	}

}
