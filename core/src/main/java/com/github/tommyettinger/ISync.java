package com.github.tommyettinger;

/**
 * Created by Tommy Ettinger on 10/29/2019.
 */
public interface ISync {
    void sync(int fps);
    
    class EmptySync implements ISync {
        @Override
        public void sync(int fps) {

        }
    }

}
