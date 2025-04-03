import common.Stepper;
import org.junit.Assert;
import org.junit.Test;

public class TestBase {

    @Test
    public void TestStepper() {
        Stepper st = new Stepper(-2f,10f, 255);
        Assert.assertEquals(-2f, st.get(0), 0.1f);
        Assert.assertEquals(-1.953, st.get(1), 0.1f);
        Assert.assertEquals(0.353f, st.get(50), 0.1f);
        Assert.assertEquals(9.952941f, st.get(254), 0.1f);
        Assert.assertEquals(10f, st.get(255), 0.1f);
        Assert.assertEquals(10f, st.get(256), 0.1f);
    }
}
