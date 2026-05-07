package ru.nsu.tumilevich;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {
	private BlockingQueue<String> queue;

	@BeforeEach
	void setUp() {
		queue = new BlockingQueue<>(2);
	}

	@Test
	void testPutAndTake() throws InterruptedException {
		queue.put("Order1");
		assertEquals(1, queue.size());
		assertFalse(queue.isEmpty());

		String item = queue.take();
		assertEquals("Order1", item);
		assertTrue(queue.isEmpty());
	}

	@Test
	void testPoll() throws InterruptedException {
		assertNull(queue.poll());

		queue.put("Order1");
		assertEquals("Order1", queue.poll());
	}

	@Test
	void testCloseWhenFull() throws InterruptedException {
		queue.put("Order1");
		queue.put("Order2");
		queue.close();

		assertThrows(InterruptedException.class, () -> queue.put("Order3"));
	}

	@Test
	void testTakeFromClosedEmptyQueue() throws InterruptedException {
		queue.close();
		assertNull(queue.take());
	}

	@Test
	void testGetAll() throws InterruptedException {
		queue.put("Order1");
		Queue<String> all = queue.getAll();
		assertEquals(1, all.size());
		assertTrue(all.contains("Order1"));
	}
}