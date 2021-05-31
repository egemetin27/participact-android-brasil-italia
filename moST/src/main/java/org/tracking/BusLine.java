/*
 * Copyright (C) 2012 <copyright_owner>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author marcomoschettini
 */

package org.tracking;

public class BusLine {
    private String line_code;

    public BusLine(String name) {
        super();
        this.line_code = name;
    }

    public String getName() {
        return line_code;
    }

    public void setName(String name) {
        this.line_code = name;
    }


    @Override
    public String toString() {
        return "BusLine [line_code=" + line_code + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BusLine other = (BusLine) obj;
        if (line_code == null) {
            if (other.line_code != null)
                return false;
        } else if (!line_code.equals(other.line_code))
            return false;
        return true;
    }

}
