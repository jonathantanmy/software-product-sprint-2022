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
public final class SetOverview {

  private final long id;
  private final String creator;
  private final String set_database;
  private final String setname;
  private final int term_amount;
  private final long timestamp;
  private final String uid;
  

  public SetOverview(long id, String creator, String set_database,String setname, int term_amount,  long timestamp, String uid) {
    this.id = id;
    this.creator = creator;
    this.set_database = set_database;
    this.setname = setname;
    this.term_amount = term_amount;
    this.timestamp = timestamp;
    this.uid = uid;
  }
}