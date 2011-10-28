package com.nearinfinity.blur.store.blockcache;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

import com.nearinfinity.blur.store.DirectIODirectory;

public class BlockDirectory extends DirectIODirectory {
  
  public static final long BLOCK_SHIFT = 13; // 2^13 = 8,192 bytes per block
  public static final long BLOCK_MOD = 0x1FFF;
  public static final int BLOCK_SIZE = 1 << BLOCK_SHIFT;

  public static long getBlock(long pos) {
    return pos >>> BLOCK_SHIFT;
  }

  public static long getPosition(long pos) {
    return pos & BLOCK_MOD;
  }

  public static long getRealPosition(long block, long positionInBlock) {
    return (block << BLOCK_SHIFT) + positionInBlock;
  }

  private DirectIODirectory _directory;
  private int _blockSize;
  private String _dirName;
  private Cache _cache = new Cache() {
    
    @Override
    public void update(String name, long blockId, byte[] buffer) {
      
    }
    
    @Override
    public boolean fetch(String name, long blockId, int blockOffset, byte[] b, int off, int lengthToReadInBlock) {
      return false;
    }

    @Override
    public void delete(String name) {
      
    }

    @Override
    public long size() {
      return 0;
    }
  };
  
  private static ThreadLocal<Boolean> _modifyFileCache = new ThreadLocal<Boolean>() {
    @Override
    protected Boolean initialValue() {
      return true;
    }
  };
  
  public static void setModifyFileCache(boolean b) {
    _modifyFileCache.set(b);
  }
  
  public BlockDirectory(String dirName, DirectIODirectory directory) throws IOException {
    _dirName = dirName;
    _directory = directory;
    _blockSize = BLOCK_SIZE;
    setLockFactory(directory.getLockFactory());
  }

  public BlockDirectory(String dirName, DirectIODirectory directory, Cache cache) throws IOException {
    _dirName = dirName;
    _directory = directory;
    _blockSize = BLOCK_SIZE;
    _cache = cache;
    setLockFactory(directory.getLockFactory());
  }
  
  public IndexInput openInput(String name, int bufferSize) throws IOException {
    final IndexInput source = _directory.openInput(name, _blockSize);
    return new CachedIndexInput(source, _blockSize, _dirName, name, _cache, bufferSize);
  }
  
  @Override
  public IndexInput openInput(final String name) throws IOException {
    final IndexInput source = _directory.openInput(name, _blockSize);
    return new CachedIndexInput(source, _blockSize, _dirName, name, _cache);
  }

  static class CachedIndexInput extends BufferedIndexInput {

    private IndexInput _source;
    private int _blockSize;
    private long _fileLength;
    private byte[] _buffer;
    private String _cacheName;
    private Cache _cache;
    private boolean _updateCache;

    public CachedIndexInput(IndexInput source, int blockSize, String dirName, String name, Cache cache) {
      _source = source;
      _blockSize = blockSize;
      _fileLength = source.length();
      _cacheName = dirName + "/" + name;
      _cache = cache;
      _buffer = new byte[_blockSize];
    }
    
    public CachedIndexInput(IndexInput source, int blockSize, String dirName, String name, Cache cache, int bufferSize) {
      super(bufferSize);
      _source = source;
      _blockSize = blockSize;
      _fileLength = source.length();
      _cacheName = dirName + "/" + name;
      _cache = cache;
      _buffer = new byte[_blockSize];
      _updateCache = _modifyFileCache.get();
    }

    @Override
    public Object clone() {
      CachedIndexInput clone = (CachedIndexInput) super.clone();
      clone._source = (IndexInput) _source.clone();
      clone._buffer = new byte[_blockSize];
      clone._updateCache = _modifyFileCache.get();
      return clone;
    }

    @Override
    public long length() {
      return _source.length();
    }

    @Override
    public void close() throws IOException {
      _source.close();
    }

    @Override
    protected void seekInternal(long pos) throws IOException {
    }

    @Override
    protected void readInternal(byte[] b, int off, int len) throws IOException {
      long position = getFilePointer();
      while (len > 0) {
        int length = fetchBlock(position, b, off, len);
        position += length;
        len -= length;
        off += length;
      }
    }

    private int fetchBlock(long position, byte[] b, int off, int len) throws IOException {
      if (_updateCache) {
        //read whole block into cache and then provide needed data
        long blockId = getBlock(position);
        int blockOffset = (int) getPosition(position);
        int lengthToReadInBlock = Math.min(len, _blockSize - blockOffset);
        if (checkCache(blockId, blockOffset, b, off, lengthToReadInBlock)) {
          return lengthToReadInBlock;
        } else {
          readIntoCacheAndResult(blockId, blockOffset, b, off, lengthToReadInBlock);
        }
        return lengthToReadInBlock;
      } else {
        _source.seek(position);
        _source.readBytes(b, off, len);
        return len;
      }
    }

    private void readIntoCacheAndResult(long blockId, int blockOffset, byte[] b, int off, int lengthToReadInBlock) throws IOException {
      long position = getRealPosition(blockId,0);
      int length = (int) Math.min(_blockSize, _fileLength - position);
      _source.seek(position);
      _source.readBytes(_buffer, 0, length);
      System.arraycopy(_buffer, blockOffset, b, off, lengthToReadInBlock);
      _cache.update(_cacheName,blockId,_buffer);
    }

    private boolean checkCache(long blockId, int blockOffset, byte[] b, int off, int lengthToReadInBlock) {
      return _cache.fetch(_cacheName,blockId,blockOffset,b,off,lengthToReadInBlock);
    }
  }

  @Override
  public void close() throws IOException {
    _directory.close();
  }

  public void clearLock(String name) throws IOException {
    _directory.clearLock(name);
  }

  public void copy(Directory to, String src, String dest) throws IOException {
    _directory.copy(to, src, dest);
  }

  public LockFactory getLockFactory() {
    return _directory.getLockFactory();
  }

  public String getLockID() {
    return _directory.getLockID();
  }

  public Lock makeLock(String name) {
    return _directory.makeLock(name);
  }

  public void setLockFactory(LockFactory lockFactory) throws IOException {
    _directory.setLockFactory(lockFactory);
  }

  public void sync(Collection<String> names) throws IOException {
    _directory.sync(names);
  }

  @SuppressWarnings("deprecation")
  public void sync(String name) throws IOException {
    _directory.sync(name);
  }

  public String toString() {
    return _directory.toString();
  }

  public IndexOutput createOutput(String name) throws IOException {
    return _directory.createOutput(name);
  }

  public void deleteFile(String name) throws IOException {
    _directory.deleteFile(name);
    _cache.delete(name);
  }

  public boolean fileExists(String name) throws IOException {
    return _directory.fileExists(name);
  }

  public long fileLength(String name) throws IOException {
    return _directory.fileLength(name);
  }

  public long fileModified(String name) throws IOException {
    return _directory.fileModified(name);
  }

  public String[] listAll() throws IOException {
    return _directory.listAll();
  }

  @SuppressWarnings("deprecation")
  public void touchFile(String name) throws IOException {
    _directory.touchFile(name);
  }

  @Override
  public IndexOutput createOutputDirectIO(String name) throws IOException {
    return _directory.createOutputDirectIO(name);
  }

  @Override
  public IndexInput openInputDirectIO(String name) throws IOException {
    return _directory.openInputDirectIO(name);
  }

}