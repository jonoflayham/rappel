package com.scintillance.rappel;

import clojure.lang.Compiler;
import clojure.lang.RT;

import java.io.StringReader;

public class Rappel {
    private static final String NREPL_INIT_SNIPPET = "(use '[clojure.tools.nrepl.server :only (start-server stop-server)]) (start-server :port %s)";
    private static final String GORILLA_REPL_INIT_SNIPPET = "(use '[gorilla-repl.core :only (run-gorilla-server)]) (run-gorilla-server {:nrepl-port %s :port %s})";

    public void startNrepl(int port, Object... symbolsAndTheirObjects) {
        startRepl(String.format(NREPL_INIT_SNIPPET, port), symbolsAndTheirObjects);
    }

    public void startGorillaRepl(int nreplPort, int gorillaPort, Object... symbolsAndObjects) {
        startRepl(String.format(GORILLA_REPL_INIT_SNIPPET, nreplPort, gorillaPort), symbolsAndObjects);
    }

    public void startRepl(String clojureBootstrapSnippet, Object...symbolsAndTheirObjects) {
        if (symbolsAndTheirObjects.length % 2 != 0) {
            throw new IllegalArgumentException("symbolsAndTheirObjects must be a sequence of pairs of symbol name and associated object");
        }

        loadClojureRuntime();
        StringReader snippetReader = new StringReader(clojureBootstrapSnippet);
        Compiler.load(snippetReader);

        for (int a = 0; a < symbolsAndTheirObjects.length; a += 2) {
            def((String) symbolsAndTheirObjects[a], symbolsAndTheirObjects[a + 1]);
        }
    }

    public void def(String symbolName, Object value) {
        RT.var("user", symbolName, value);
    }

    private void loadClojureRuntime() {
        try {
            Class.forName("clojure.lang.RT");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
