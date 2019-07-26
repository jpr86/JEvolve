/* %%
 * 
 * JEvolve
 *
 * Copyright 2007 Jeff Ridder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ridderware.jevolve.ui;

import java.util.Observable;

/**
 *
 * @author jhanduber
 */
public class UIFactory
{
    private static EvolutionUI ui;

    private UIFactory()
    {}
    
    public static void ShowUI(Observable populationSource, String ... fitnessDescriptions)
    {
        ShowUI(new UIFactoryOptions(populationSource, fitnessDescriptions));
    }
    
    public static void ShowUI(UIFactoryOptions generalOptions)
    {
        ui = new EvolutionUI(generalOptions);
        ui.setVisible(true);
    }

    public static void CloseUI()
    {
        ui.saveViews();
    }
    
}