package tests.debug;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class LibgdxCollection {

    @Test
    public void filterArray() {
        Array array = new Array();
        array.add("a");
        array.add("b");

        Iterable select = array.select(new Predicate() {
            @Override
            public boolean evaluate(Object arg0) {
                return arg0.equals("a");
            }
        });

        String text = "";
        for (Object o : select) {
            text += o;
        }
        assertThat(text).isEqualTo("a");

        String text2 = "";
        for (Object o : array) {
            text2 += o;
        }
        assertThat(text2).isEqualTo("ab");
    }

    @Test
    public void iteratorNextOnEmptyArray() {
        Array array = new Array();
        Object next = array.select(arg0 -> true == false).iterator().next();
        Assert.assertNull(next);
    }
}
