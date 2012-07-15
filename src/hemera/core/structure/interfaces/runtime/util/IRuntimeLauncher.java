package hemera.core.structure.interfaces.runtime.util;

import hemera.core.structure.interfaces.runtime.IRuntime;

import org.apache.commons.daemon.Daemon;

/**
 * <code>IRuntimeLauncher</code> defines the interface
 * of the utility unit responsible for creating and
 * launching a <code>IRuntime</code> instance based on
 * configuration in <code>Daemon</code> mode.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IRuntimeLauncher extends Daemon {

	/**
	 * Set if the launching process should automatically
	 * scan the applications directory for all deployed
	 * applications to attach to the runtime.
	 * @param scan <code>true</code> if launching should
	 * scan for applications. <code>false</code> otherwise.
	 */
	public void setScanApps(final boolean scan);
	
	/**
	 * Retrieve the launched runtime instance.
	 * @return The <code>IRuntime</code> instance.
	 */
	public IRuntime getRuntime();
}
