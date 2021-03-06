import "./SearchEngineSettings.css";

import React from "react";

import { view } from "react-easy-state";

import { sources } from "../../config-sources.js";

export const SearchEngineSettings = view((props) => {
  const source = sources[props.source];

  return (
    <div className={"SearchEngineSettings"}>
      <h4>{source.label} search options</h4>
      {
        source.createSourceConfig({ onChange: () => {} })
      }
    </div>
  );
});