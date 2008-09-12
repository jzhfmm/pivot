package pivot.wtk;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Annotation that provides additional information about a component.</p>
 *
 * @author eryzhikov
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentInfo {
    String icon();
}
