package aohara.common.test;

import static org.junit.Assert.*;

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
		assertFalse(mgr.hasProfile(name));
		
		mgr.saveProfile(p1);
		assertEquals(1, mgr.size());
		assertTrue(mgr.hasProfile(name));
		assertEquals(p1, mgr.getProfile(name));
	}
	
	@Test
	public void testAddTwo(){
		String name1 = "foo", name2 = "bar";
		Profile p1 = mgr.newProfile(name1), p2 = mgr.newProfile(name2);
		assertEquals(0, mgr.size());
		
		mgr.saveProfile(p1);
		mgr.saveProfile(p2);
		assertEquals(2, mgr.size());
		assertEquals(p1, mgr.getProfile(name1));
		assertEquals(p2, mgr.getProfile(name2));
	}
	
	@Test
	public void testHasWhileEmpty(){
		assertFalse(mgr.hasProfile("foo"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetWhileEmpty(){		
		mgr.getProfile("foo");
	}
	
	@Test
	public void testHasWrongName(){
		String name = "foo";
		mgr.saveProfile(mgr.newProfile(name));
		assertFalse(mgr.hasProfile(name + "bar"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetWrongName(){
		String name = "foo";
		mgr.saveProfile(mgr.newProfile(name));
		mgr.getProfile(name + "bar");
	}

}
