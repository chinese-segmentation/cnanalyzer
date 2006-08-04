package zzzhc.parser;

import zzzhc.dict.Dict;
import zzzhc.dict.DictItem;
import zzzhc.dict.Word;

public class WordPicker {

	private Dict dict;

	private boolean maxMode;

	public WordPicker(Dict dict) {
		this(dict, true);
	}

	public WordPicker(Dict dict, boolean maxMode) {
		this.dict = dict;
		this.maxMode = maxMode;
	}

	public Word pickWord(CharacterIterator characterIterator) {
		DictItem currentItem = dict.getRootItem();
		characterIterator.mark();
		Character c;
		DictItem childItem;
		StringBuffer buf = new StringBuffer();
		StringBuffer unknownBuf = new StringBuffer(2);
		int lastWordLen = 0;
		while (characterIterator.hasNext()) {
			c = (Character) characterIterator.next();
			childItem = currentItem.getChild(c);

			if (childItem == null) {
				characterIterator.reset();
				if (lastWordLen != 0) {// �ѳɴ�
					buf.setLength(lastWordLen);
					return createWord(buf, true);
				} else {// ����
					buf.setLength(0);
					c = (Character) characterIterator.next();
					characterIterator.mark();
					unknownBuf.append(c.charValue());
					childItem = dict.getRootItem();
				}
			} else {
				if (childItem.isEnd()) {// find word
					if (unknownBuf.length() != 0) {
						characterIterator.reset();
						return createWord(unknownBuf, false);
					} else {
						buf.append(c.charValue());
						return createWord(buf, true);
					}
				} else if (childItem.isWordEnd()) {
					if (unknownBuf.length() != 0) {
						characterIterator.reset();
						return createWord(unknownBuf, false);
					} else if (maxMode == false) {
						buf.append(c.charValue());
						return createWord(buf, true);
					} else {
						buf.append(c.charValue());
						lastWordLen = buf.length();
						characterIterator.mark();
					}
				} else {
					buf.append(c.charValue());
				}
			}
			currentItem = childItem;
		}
		if (buf.length() != 0) {
			unknownBuf.append(buf);
		}
		if (unknownBuf.length() != 0) {
			return createWord(unknownBuf, false);
		} else {
			return null;
		}
	}

	private Word createWord(StringBuffer buf, boolean wordExist) {
		Word word = new Word(buf.toString(), 0, buf.length());
		if (wordExist) {
			word.setType(Word.WORD_TYPE);
		}
		else {
			word.setType(Word.UNKNOWN_TYPE);
		}
		return word;
	}
}
