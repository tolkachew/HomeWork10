import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Book {
    private static final int NUM_READERS = 5;
    private static final int NUM_WRITERS = 3;
    private static final int MIN_WAIT_TIME = 1000; // 1 second
    private static final int MAX_WAIT_TIME = 3000; // 3 seconds

    private String content = ""; // содержимое книги
    private final Lock bookLock = new ReentrantLock(); // мьютекс для синхронизации доступа к книге

    public void start() {
        // создаем потоки для писателей
        for (int i = 0; i < NUM_WRITERS; i++) {
            new Thread(new Writer(i)).start();
        }

        // создаем потоки для читателей
        for (int i = 0; i < NUM_READERS; i++) {
            new Thread(new Reader(i)).start();
        }
    }

    // поток-писатель
    private class Writer implements Runnable {
        private final int id;

        public Writer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                // писатель получает вдохновение
                String inspiration = getInspiration();

                // писатель дописывает книгу
                bookLock.lock();
                try {
                    content += inspiration;
                    System.out.println("Writer " + id + " wrote: " + inspiration);
                } finally {
                    bookLock.unlock();
                }

                // писатель засыпает на случайное время
                try {
                    Thread.sleep(getRandomWaitTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // генерирует случайную строку вдохновения
        private String getInspiration() {
            return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ";
        }

        // генерирует случайное время ожидания
        private int getRandomWaitTime() {
            return MIN_WAIT_TIME + (int) (Math.random() * (MAX_WAIT_TIME - MIN_WAIT_TIME));
        }
    }

    // поток-читатель
    private class Reader implements Runnable {
        private final int id;

        public Reader(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                // читатель ожидает доступа к книге
                bookLock.lock();
                try {
                    // читатель начинает чтение книги
                    System.out.println("Reader " + id + " is reading the book: " + content);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    bookLock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Book().start();
    }
}
