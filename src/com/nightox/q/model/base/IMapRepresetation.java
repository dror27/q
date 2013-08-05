package com.nightox.q.model.base;

import java.util.Map;

public interface IMapRepresetation {
	
	final int		LEVEL_SHALOW 		= 0;
	final int		LEVEL_NEST		 	= 1;
	
	Map<String, Object>		getMapRepresentation(int level);

}
