#### Java

* Different versions of getting Books from the major retailers.   
    1.  Chain of Responsibility pattern
        ```
        abstract  public class BookParserHandler  {
            protected BookParserHandler successor;

            public void setSuccessor(BookParserHandler successor) {
                this.successor = successor;
            }

            public Optional<Book> processRequest(String key) {
                Optional<Book> book = getBook(key);
                if (!book.isPresent() && successor != null) {
                return successor.processRequest(key);
                }else {
                return book;
                }
            }

            abstract public Optional<Book> getBook(String isbn);
        }
        ```

        Start searching from Amazon:

        ```
        BookParserHandler amazonHandler = new AmazonBookParser();
        BookParserHandler bookDotComHandler = new BooksDotComBookParser();
        amazonHandler.setSuccessor(bookDotComHandler);
        bookOptional = amazonHandler.processRequest(ISBN);
        ```

    2. Searching the book concurrently from different retailers.
        
        REF: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorCompletionService.html
        ```
        private Optional<Book> getBookFromParser(Executor e, Collection<Callable<Optional<Book>>> solvers)
            throws InterruptedException {

            CompletionService<Optional<Book>> ecs = new ExecutorCompletionService<>(e);
            int n = solvers.size();
            List<Future<Optional<Book>>> futures = new ArrayList<>(n);
            Book result = null;
            try {
            for (Callable<Optional<Book>> s : solvers) futures.add(ecs.submit(s));
            for (int i = 0; i < n; ++i) {
                try {
                Optional<Book> r = ecs.take().get();
                if (r.isPresent()) {
                    result = r.get();
                    break;
                }
                } catch (ExecutionException ignore) {
                }
            }
            } finally {
            for (Future<Optional<Book>> f : futures) f.cancel(true);
            }

            return Optional.ofNullable(result);
        }    
        ```
        ```
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Callable<Optional<Book>>> callableList = new ArrayList<>();

        callableList.add(() -> amazonHandler.getBook(ISBN));
        callableList.add(() -> bookDotComHandler.getBook(ISBN));
        ```