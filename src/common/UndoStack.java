package common;

import java.util.Stack;

public class UndoStack<T> extends Stack<T>
{
    private final int maxSize;

    public UndoStack(int size)
    {
        super();
        this.maxSize = size;
    }

    @Override
    public T pop() {
        try {
            return super.pop();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public T push(T object)
    {
        //If the stack is too big, remove elements until it's the right size.
        while (this.size() >= maxSize)
        {
            this.remove(0);
        }
        return super.push(object);
    }
}