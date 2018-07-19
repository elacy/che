/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.selenium.miscellaneous;

import static org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces.Status.RUNNING;

import com.google.inject.Inject;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.executor.OpenShiftCliCommandExecutor;
import org.eclipse.che.selenium.core.user.DefaultTestUser;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.core.workspace.TestWorkspaceProvider;
import org.eclipse.che.selenium.core.workspace.WorkspaceTemplate;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class MachinesAsynchronousStartTest {
  @Inject private Dashboard dashboard;
  @Inject private Workspaces workspaces;
  @Inject private TestWorkspaceProvider testWorkspaceProvider;
  @Inject private TestWorkspaceServiceClient testWorkspaceServiceClient;
  @Inject private DefaultTestUser defaultTestUser;
  @Inject private OpenShiftCliCommandExecutor openShiftCliCommandExecutor;
  @Inject private TestWorkspace testWorkspace;

  private TestWorkspace brokenWorkspace;

  @AfterClass
  public void cleanUp() throws Exception {
    testWorkspaceServiceClient.delete(brokenWorkspace.getName(), defaultTestUser.getName());
  }

  @Test
  public void checkWorkspaces() throws Exception {
    // prepare
    dashboard.open();

    String wsId = testWorkspace.getId();
    String wsName = testWorkspace.getName();

    brokenWorkspace = createBrokenWorkspace();
    testWorkspaceServiceClient.start(
        brokenWorkspace.getName(), brokenWorkspace.getId(), defaultTestUser);

    // check that broken workspace is displayed with "Running" status
    dashboard.waitDashboardToolbarTitle();
    dashboard.selectWorkspacesItemOnDashboard();
    workspaces.waitPageLoading();
    workspaces.waitWorkspaceStatus(brokenWorkspace.getName(), RUNNING);

    // check oc log output

  }

  private TestWorkspace createBrokenWorkspace() throws Exception {
    return testWorkspaceProvider.createWorkspace(
        defaultTestUser, 2, WorkspaceTemplate.BROKEN, true);
  }
}