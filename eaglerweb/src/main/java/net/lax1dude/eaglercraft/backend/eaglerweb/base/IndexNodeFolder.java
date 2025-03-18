package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class IndexNodeFolder extends IndexNode implements Iterable<IndexNode> {

	private final Date dateObj;
	private final String name;
	private final Map<String, IndexNode> children;

	protected IndexNodeFolder(long lastModified, String name, Map<String, IndexNode> children) {
		this.dateObj = new Date(lastModified);
		this.name = name;
		this.children = children;
	}

	@Override
	IndexNode find(CharSequence charSeq) {
		return children.get(charSeq);
	}

	boolean isEmpty() {
		return children.isEmpty();
	}

	@Override
	boolean isDirectory() {
		return true;
	}

	@Override
	ResponseCacheKey getResponse(List<String> index) {
		int cnt = index.size();
		for(int i = 0; i < cnt; ++i) {
			IndexNode node = children.get(index.get(i));
			if(node != null && !node.isDirectory()) {
				return node.getResponse(null);
			}
		}
		return null;
	}

	@Override
	public Iterator<IndexNode> iterator() {
		return children.values().iterator();
	}

	@Override
	String getName() {
		return name;
	}

	@Override
	Date lastModified() {
		return dateObj;
	}

	@Override
	long getSize() {
		return -1l;
	}

}