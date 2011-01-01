package net.chilicat.testenv;

import net.chilicat.testenv.core.TestExecutor;
import net.chilicat.testenv.rhino.RhinoTestExecutor;
import net.chilicat.testenv.webdriver.ChilicatTestExecutor;
import net.chilicat.testenv.webdriver.ChromeTestExecutor;
import net.chilicat.testenv.webdriver.FireFoxTestExecutor;
import net.chilicat.testenv.webdriver.HtmlUnitTestExecutor;

/**
* Copyright (c) 2010 <chilicat>
* <p/>
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* <p/>
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* <p/>
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
* <p/>
* User: Chilicat
* Date: 13.11.2010
* Time: 13:15:41
*/
public enum ExecutorType {
    chilicat("Chilicat", "Default test executor.", false) {
        @Override
        public TestExecutor create() {
            return new RhinoTestExecutor(); //ChilicatTestExecutor();
        }},
    chrome("Google Chrome", "Chrome test executor. No code coverage support.", false) {
        @Override
        public TestExecutor create() {
            return new ChromeTestExecutor();
        }},
    firefox("Firefox", "Firefox test executor. No code coverage support.", false) {
        @Override
        public TestExecutor create() {
            return new FireFoxTestExecutor();
        }},

    htmlunit("HtmlUnit", "HtmlUnit test executor. No code coverage support.", false) {
        @Override
        public TestExecutor create() {
            return new HtmlUnitTestExecutor();
        }};

    private String doc;
    private String name;
    private boolean hide;

    ExecutorType(String name, String doc, boolean hide) {
        this.name = name;
        this.doc = doc;
        this.hide = hide;
    }

    public String getDisplayName() {
        return name;
    }

    public boolean isHidden() {
        return hide;
    }

    public String getDoc() {
        return doc;
    }

    public abstract TestExecutor create();
}
