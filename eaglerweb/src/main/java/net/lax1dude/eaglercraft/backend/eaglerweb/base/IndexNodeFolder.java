package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

class IndexNodeFolder extends IndexNode implements Iterable<IndexNode> {

	private final Map<String, IndexNode> children;

	protected IndexNodeFolder(Map<String, IndexNode> children) {
		this.children = children;
	}

	@Override
	protected IndexNode find(CharSequence charSeq) {
		return children.get(charSeq);
	}

	protected boolean isEmpty() {
		return children.isEmpty();
	}

	@Override
	protected boolean isDirectory() {
		return true;
	}

	@Override
	protected ResponseCacheKey getResponse(List<String> index) {
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

}