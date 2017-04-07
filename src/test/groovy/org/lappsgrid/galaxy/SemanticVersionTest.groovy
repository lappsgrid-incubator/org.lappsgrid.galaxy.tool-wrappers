package org.lappsgrid.galaxy

import org.junit.Test

class SemanticVersionTest {

    def LT = { a,b -> a < b}
    def GT = { a,b -> a > b}
    def EQ = { a,b -> a == b}
    def NE = { a,b -> a !=  b}

    @Test
    void lessThan() {
        '1.0.1 1.1.0 2.0.0'.tokenize(' ').each {
            test('1.0.0', it, LT)
        }
    }

    @Test
    void greaterThan() {
        '1.0.1 1.1.0 2.0.0'.tokenize(' ').each {
            test(it, '1.0.0', GT)
        }
    }

    @Test
    void equality() {
        test('1.0.0', '1.0.0', EQ)
        test('1.0.0-SNAPSHOT', '1.0.0-SNAPSHOT', EQ)
        test('1.0.0-SNAPSHOT', '1.0.0-RC1', EQ)
        test('1.0.0', '1.0.1', NE)
    }

    @Test
    void snapshotIsLessThanRelease() {
        test('1.0.0-SNAPSHOT', '1.0.0', LT)
    }

    void test(String v1, String v2, Closure cl) {
        assert cl(new SemanticVersion(v1), new SemanticVersion(v2))
	}

	void runTests() {
//		test('1.0.0', '2.0.0')
//		test('1.1.0', '1.1.1')
//		test('3.1.0', '3.1.0')
//		test('3.1.0', '3.1.0-SNAPSHOT')
//		test('1.0.0', '1.0.0')
//		test('1.0.0-SNAPSHOT', '1.0.0-RC1')
	}
}
