package com.britishheritage.android.britishheritage.Api;

import com.britishheritage.android.britishheritage.Response.NearbyWikipediaResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**An api designed to communicate the geocode nearby wikipedia api following the below example format:
 *
 * http://api.geonames.org/findNearbyWikipediaJSON?lat=53.19&lng=-2.89&username=britishheritage&maxRows=20
 *
 */
public interface FindNearbyWikipediaApi {

    @GET("/findNearbyWikipediaJSON")
    Observable<NearbyWikipediaResponse> getWikiResponse(@Query("lat") double lat,
                                                        @Query("lng") double lng,
                                                        @Query("username") String username,
                                                        @Query("maxRows") String num);
}
