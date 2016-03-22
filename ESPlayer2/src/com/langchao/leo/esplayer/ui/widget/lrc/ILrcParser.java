package com.langchao.leo.esplayer.ui.widget.lrc;

import java.util.List;

/**
 * 
 * @author Ligang  2014/8/19
 *
 */
public interface ILrcParser {

	List<LrcRow> getLrcRows(String str);
}
