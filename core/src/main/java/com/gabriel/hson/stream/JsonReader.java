package com.gabriel.hson.stream;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

//reading gson program for understand streaming API
//this file will be Refactored
public class JsonReader implements Cloneable {
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_BEGIN_ARRAY =3;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_EOF = 17;
    private int[] stack = new int[32];
    private int stackSize = 0;
    private final int BUFFER_SIZE =1024;
    private final char[] buffer = new char[BUFFER_SIZE];
    private int pos = 0;
    private int limit = 0;
    private int lineStart = 0;
    private int lineNumber = 0;

    private final Reader in;
    int peeked = PEEKED_NONE;

    public JsonReader(Reader in) {
        this.in = Objects.requireNonNull(in, "input is null");
    }

    public boolean hasNext() throws Exception {
        int p = peeked;
        if (p == PEEKED_NONE) {
            p = doPeek();
        }
        return p != PEEKED_END_OBJECT && p != PEEKED_END_ARRAY && p != PEEKED_EOF;
    }

    public int doPeek() throws Exception {
        int peekStack = stack[stackSize - 1];
        if (peekStack == JsonScope.EMPTY_ARRAY) {
            stack[stackSize - 1] = JsonScope.NONEMPTY_ARRAY;
        } else if (peekStack == JsonScope.NONEMPTY_ARRAY) {
            int c = nextNonWhitespace(true);
            switch (c) {
                case ']':
                    return peeked = PEEKED_END_ARRAY;
                case ';':
                    checkLenient();
                case ',':
                    break;
                default:
                    throw syntaxError("error");
            }
        }
        return 0;
    }

    private Exception syntaxError(String error) {
        //TODO
        Exception exception = new Exception();
        return exception;
    }

    private void checkLenient() {
    }

    private int nextNonWhitespace(boolean throwOnEof) throws IOException {
        char buffer[] = this.buffer;
        int p = this.pos;
        int l = this.limit;
        while(true) {
            if (p == l) {
                pos = p;
                if (!fillBuffer(1)) {
                    break;
                }
                p = pos;
                l = limit;
            }
            int c = buffer[p++];
            if (c =='\n') {
                lineNumber++;
                lineStart = p;
                continue;
            } else if (c == ' ' || c== '\r' || c == '\t') {
                continue;
            }
            if (c == '/') {
                pos = p;
                pos--;
                boolean charsLoaded =fillBuffer(2);
                pos++;
                if (!charsLoaded) {
                    return c;
                }
            }

        }
        if (throwOnEof) {
            throw new IOException();
        }else {
            return -1;
        }
    }

    private boolean fillBuffer(int minimum) throws IOException {
        char[] buffer = this.buffer;
        lineStart -= pos;
        if (limit != pos) {
            limit -= pos;
            System.arraycopy(buffer,pos,buffer,0,limit);
        } else {
            limit = 0;
        }
        pos = 0;
        int total;
        while((total= in.read(buffer, limit, buffer.length - limit)) != -1) {
            limit += total;
            if (lineNumber == 0 && lineStart == 0 && limit > 0 && buffer[0] =='\ufeff') {
                pos++;
                lineStart++;
                minimum++;
            }
            if (limit >= minimum) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JsonReader clone() {
        try {
            return (JsonReader) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
