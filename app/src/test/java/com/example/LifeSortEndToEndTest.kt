package com.example

import com.example.testing.LifeSortTestSuite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LifeSortEndToEndTest {

    private val testSuite = LifeSortTestSuite()

    @Test
    fun runAll20LifeSortEndToEndTests() {
        val results = testSuite.runAll20Tests()
        assertEquals("Must run exactly 20 test cases", 20, results.size)

        val failedTests = results.filter { !it.passed }
        if (failedTests.isNotEmpty()) {
            val failureReport = failedTests.joinToString("\n") {
                "Test #${it.testNumber} [${it.name}] FAILED: ${it.details}"
            }
            throw AssertionError("Some E2E tests failed:\n$failureReport")
        }

        results.forEach { result ->
            assertTrue("Test #${result.testNumber} [${result.name}] should pass", result.passed)
            println("PASS [${result.testNumber}/20] ${result.name}: ${result.details}")
        }
    }
}
