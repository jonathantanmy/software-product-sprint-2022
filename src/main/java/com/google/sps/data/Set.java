// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

/** An item on a todo list. */
public final class Set {

  private final long id;
  private final Boolean hasImage;
  private final String imageName;
  
  private final String setname;
  private final String term;
  private final long timestamp;
  private final String url;

  public Set(long id, Boolean hasImage, String imageName, String setname, String term, long timestamp, String url) {
    this.id = id;
    this.hasImage = hasImage;
    this.imageName = imageName;
    this.setname = setname;
    this.term = term;
    this.timestamp = timestamp;
    this.url = url;
  }
}