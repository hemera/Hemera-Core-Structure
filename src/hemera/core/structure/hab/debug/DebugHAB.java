package hemera.core.structure.hab.debug;

import hemera.core.environment.ham.HAM;
import hemera.core.structure.hab.HAB;

/**
 * <code>DebugHAB</code> defines the HAB extension with
 * debugging support.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.4
 */
public class DebugHAB extends HAB {
	/**
	 * The <code>String</code> shared configuration path.
	 */
	public final String sharedConfigPath;
	
	/**
	 * Constructor of <code>DebugHAB</code>.
	 * @param ham The <code>HAM</code> instance.
	 */
	public DebugHAB(final HAM ham) {
		super(ham);
		if (ham.shared == null) {
			this.sharedConfigPath = null;
		} else {
			this.sharedConfigPath = ham.shared.configFile;
		}
	}
}
