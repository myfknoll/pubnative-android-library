// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.request.model.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PubnativeAPIV3AdModel implements Serializable {

    //==============================================================================================
    // Fields
    //==============================================================================================

    public String                        link;
    public List<PubnativeAPIV3DataModel> assets;
    public List<PubnativeAPIV3DataModel> beacons;
    public List<PubnativeAPIV3DataModel> meta;

    //==============================================================================================
    // Interfaces
    //==============================================================================================

    /**
     * Interface containing all possible Beacons
     */
    public interface Beacon {

        String IMPRESSION = "impression";
        String CLICK      = "click";
    }

    //==============================================================================================
    // Asset
    //==============================================================================================

    public PubnativeAPIV3DataModel getAsset(String type) {

        return find(type, assets);
    }

    public PubnativeAPIV3DataModel getMeta(String type) {

        return find(type, meta);
    }

    public List<PubnativeAPIV3DataModel> getBeacons(String type) {

        return findAll(type, beacons);
    }

    protected PubnativeAPIV3DataModel find(String type, List<PubnativeAPIV3DataModel> list) {

        PubnativeAPIV3DataModel result = null;
        if (list != null) {
            for (PubnativeAPIV3DataModel data : list) {
                if (type.equals(data.type)) {
                    result = data;
                    break;
                }
            }
        }
        return result;
    }

    protected List<PubnativeAPIV3DataModel> findAll(String type, List<PubnativeAPIV3DataModel> list) {

        List<PubnativeAPIV3DataModel> result = null;
        if (list != null) {
            for (PubnativeAPIV3DataModel data : list) {
                if (type.equals(data.type)) {
                    if (result == null) {
                        result = new ArrayList<PubnativeAPIV3DataModel>();
                    }
                    result.add(data);
                }
            }
        }
        return result;
    }
}
