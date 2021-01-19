package shaun.test.stateless.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

/**
 * @author miemie
 * @since 2021-01-19
 */
public enum MyFeatures implements Feature {

    @EnabledByDefault
    @Label("First Feature")
    ONE,

    @Label("Second Feature")
    TWO;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
