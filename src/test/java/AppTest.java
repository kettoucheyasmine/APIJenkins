import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void testAddition() {
        assertEquals(4, App.add(2, 2));
    }

    @Test
    void testValidUsername() {
        assertTrue(App.isValidUser("alice"));
        assertFalse(App.isValidUser("al"));
    }
}