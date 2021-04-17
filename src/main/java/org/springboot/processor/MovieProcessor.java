package org.springboot.processor;

import org.springboot.data.MovieInfo;
import org.springframework.batch.item.ItemProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 *  A simple Transformer
 */
@Slf4j
public class MovieProcessor implements ItemProcessor<MovieInfo, MovieInfo> {
	
    
    @Override
    public MovieInfo process(final MovieInfo movie) throws Exception {
    	
    	final String id = movie.getId();
        final String title = movie.getTitle();
        final String description = movie.getDescription();

        final MovieInfo transformed = new MovieInfo(id, title, description);

        log.info("Converting {} -> {} ",  movie, transformed);

        return transformed;
    }

}
